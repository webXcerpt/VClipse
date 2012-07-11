/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.ui.outline.actions;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.eclipse.xtext.util.SimpleAttributeResolver;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.vclipse.console.CMConsolePlugin;
import org.vclipse.console.CMConsolePlugin.Kind;
import org.vclipse.vcml.ui.IUiConstants;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantTableContent;
import org.vclipse.vcml.vcml.VcmlFactory;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class VcmlOutlineAction extends Action implements ISelectionChangedListener {
	
	private static final String EXTRACTED_EXTENSION = "extracted.";

	private final Map<String, IVcmlOutlineActionHandler<?>> actionHandlers;
	
	private final List<EObject> selectedObjects;
	
	private static final VcmlFactory VCML = VcmlFactory.eINSTANCE;
	
	private PrintStream result; 
	private PrintStream err;
	
	private final IPreferenceStore preferenceStore;
	
	private XtextResourceSet resourceSet;
	
	@Inject
	public VcmlOutlineAction(IPreferenceStore preferenceStore) {
		actionHandlers = new HashMap<String, IVcmlOutlineActionHandler<?>>();
		selectedObjects = new ArrayList<EObject>();
		
		CMConsolePlugin consolePlugin = CMConsolePlugin.getDefault();
		result = new PrintStream(consolePlugin.getConsole(Kind.Result));
		err = new PrintStream(consolePlugin.getConsole(Kind.Error));
		this.preferenceStore = preferenceStore;
	}

	@Override
	public void run() {
		resourceSet = new XtextResourceSet();
		
		Resource resultResource = null;
		Resource sourceResource = null;
		
		
		final boolean outputToFile = preferenceStore.getBoolean(IUiConstants.OUTPUT_TO_FILE);
		
		final Set<String> seenObjects = Sets.newHashSet();
		
		List<EObject> selected = getSelectedObjects();
		if(selected.size() > 0) {
			sourceResource = selected.get(0).eResource();
		}
		EList<EObject> contents = sourceResource.getContents();
		if(!contents.isEmpty()) {
			Model model = (Model) contents.get(0);
			final List<Option> options = model.getOptions(); // TODO options aus sourceResource extrahieren
		
			if(outputToFile) {
				
				URI sourceUri = sourceResource.getURI();
				URI resultsUri = sourceUri.trimFileExtension().appendFileExtension(EXTRACTED_EXTENSION + sourceUri.fileExtension());
				
				resultResource = resourceSet.createResource(resultsUri, "UTF-8");
				resultResource.getContents().add(VCML.createModel());
				collectImportedObjects(seenObjects, sourceResource, resultResource);
				createResultFile(sourceResource);
			} else {
				resultResource = resourceSet.createResource(URI.createURI("results"));
				resourceSet.getResources().add(resultResource);
			}
			if(resultResource != null) {
				final Resource finalSourceResource = resultResource;
				createOutputVcmlResource(outputToFile, finalSourceResource, VCML.createModel(), new NullProgressMonitor());
				final Model vcmlModel = (Model)finalSourceResource.getContents().get(0);
				Job job = new Job(getDescription()) {
					protected IStatus run(IProgressMonitor monitor) {
						String taskName = "Executing rfc call on SAP system";
						monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
						for(EObject obj : getSelectedObjects()) {
							if(monitor.isCanceled()) {
								break;
							}
							IVcmlOutlineActionHandler<?> actionHandler = actionHandlers.get(getInstanceTypeName(obj));
							String simpleName = actionHandler.getClass().getSimpleName();
							taskName = "Executing " + simpleName + " for " + 
									SimpleAttributeResolver.NAME_RESOLVER.apply(
											obj instanceof VariantTableContent ? ((VariantTableContent)obj).getTable() : obj);
							monitor.setTaskName(taskName);
							if (actionHandler != null) {
								try {
									Method method = actionHandler.getClass().getMethod("run", new Class[]{getInstanceType(obj), Resource.class, IProgressMonitor.class, Set.class, List.class});
									method.invoke(actionHandler, new Object[]{obj, finalSourceResource, monitor, seenObjects, options});
								} catch (InvocationTargetException e) {
									if(e.getTargetException() instanceof OutlineActionCanceledException) {
										err.println("// canceled");
										break;
									} else {
										e.printStackTrace();
										e.getTargetException().printStackTrace(err); // display original cause in VClipse console
									}
								} catch(Exception exception) {
									exception.printStackTrace(err); // this can be a JCoException or an AbapExeption
								}
							}
						}
						createOutputVcmlResource(outputToFile, finalSourceResource, vcmlModel, monitor);
						result.println("Task finished: " + taskName);
						return Status.OK_STATUS;
					}
				};
				job.setPriority(Job.LONG);
				job.schedule();
			}
		}
		else {
			err.println("EObject list is empty!");
		}
	}
	
	private void createOutputVcmlResource(final boolean outputToFile, final Resource finalSourceResource, final Model vcmlModel, IProgressMonitor monitor) {
		try {
			if(outputToFile) {
				finalSourceResource.save(SaveOptions.defaultOptions().toOptionsMap());
			} else {
				if (!vcmlModel.getObjects().isEmpty()) {
					result.println(((XtextResource)finalSourceResource).getSerializer().serialize(vcmlModel));
				}
				finalSourceResource.delete(null);
			}
		} catch (Exception exception) {
			// currently, there can be exceptions if objects are not completeley initialized or linking fails or IOExceptions
			exception.printStackTrace(err);
		}
		
		IFile file = ResourceUtil.getFile(finalSourceResource);
		if(file != null && file.isAccessible()) {
			try {
				file.refreshLocal(IResource.DEPTH_ONE, monitor);
			} catch(CoreException exception) {
				exception.printStackTrace();
			}
		}
	}

	public synchronized void addHandler(String type, IVcmlOutlineActionHandler<?> handler) {
		if(handler != null) {
			actionHandlers.put(type, handler);
		}
	}
	
	private synchronized List<EObject> getSelectedObjects() {
		return Collections.unmodifiableList(selectedObjects);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		selectedObjects.clear();
		ISelection selection = event.getSelection();
		if(selection instanceof TreeSelection) {
			boolean enabled = true;
			boolean visitedSomeAction = false;
			Iterator<?> iterator = ((TreeSelection)selection).iterator();
			while(enabled && iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof EObjectNode) {
					try {
						EObject eobject = ((EObjectNode)object).readOnly(new IUnitOfWork<EObject, EObject>() {
							public EObject exec(EObject eobject) throws Exception {
								return eobject;
							}
						});
						if(eobject != null) {
							IVcmlOutlineActionHandler<?> actionHandler = actionHandlers.get(getInstanceTypeName(eobject));
							if(actionHandler!=null) {
								visitedSomeAction = true;
								Method method = actionHandler.getClass().getMethod("isEnabled", new Class[]{getInstanceType(eobject)});
								enabled &= (Boolean)method.invoke(actionHandler, eobject);
								selectedObjects.add(eobject);
							} else {
								enabled = false;
							}
						} else {
							enabled = false;
						}
					} catch(Exception exception) {
						exception.printStackTrace();
						enabled = false;
					}
					setEnabled(visitedSomeAction && enabled);
				}
			}
		}
	}
	
	private void collectImportedObjects(Set<String> seenObjects, Resource sourceResource, Resource targetResource) {
		Model targetModel = null;
		EList<EObject> targetContents = targetResource.getContents();
		if(!targetContents.isEmpty()) {
			targetModel = (Model)targetContents.get(0);
		}
		
		URI uri = sourceResource.getURI();
		String platformString = uri.toPlatformString(true);
		String lastSegment = uri.lastSegment();
		EList<EObject> contents = sourceResource.getContents();
		if(!contents.isEmpty()) {
			for(Import importStatement : ((Model)contents.get(0)).getImports()) {
				String importURI = importStatement.getImportURI();
				String importResourcePath = platformString.replace(lastSegment, importURI);
				Resource loadedResource = resourceSet.getResource(URI.createURI(importResourcePath), true);
				EList<EObject> loadedContents = loadedResource.getContents();
				if(!loadedContents.isEmpty()) {
					if(targetModel != null) {
						targetModel.getImports().add(EcoreUtil.copy(importStatement));
					}
					EObject topLevelObject = loadedContents.get(0);
					if(topLevelObject instanceof Model) {
						Model model = (Model)topLevelObject;
						for(VCObject vcobject : model.getObjects()) {
							seenObjects.add(vcobject.eClass().getName() + "/" +vcobject.getName());
						}
					}
				}
			}
		}
	}
	
	private void createResultFile(Resource resource) {
		IFile resultfile = ResourceUtil.getFile(resource);
		if(!resultfile.exists()) {
			try {
				resultfile.create(new StringInputStream(""), true, new NullProgressMonitor());
			} catch(CoreException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	private Class<?> getInstanceType(EObject obj) throws ClassNotFoundException {
		return Class.forName(getInstanceTypeName(obj));
	}
	
	private String getInstanceTypeName(EObject obj) {
		return obj.eClass().getInstanceTypeName();
	}
}
