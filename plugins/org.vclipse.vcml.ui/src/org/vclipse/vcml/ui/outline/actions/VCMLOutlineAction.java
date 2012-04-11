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

import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.vclipse.console.CMConsolePlugin;
import org.vclipse.console.CMConsolePlugin.Kind;
import org.vclipse.vcml.ui.IUiConstants;
import org.vclipse.vcml.ui.outline.SapRequestObjectLinker;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.VcmlFactory;

import com.google.inject.Inject;

public class VCMLOutlineAction extends Action implements ISelectionChangedListener {
	
	private IResourceFactory resourceFactory;
	
	private final Map<String, IVCMLOutlineActionHandler<?>> actionHandlers;
	
	private final List<EObject> selectedObjects;
	
	private static final VcmlFactory VCML = VcmlFactory.eINSTANCE;
	
	//private PrintStream out; 
	private PrintStream result; 
	private PrintStream err;
	
	private final IContentOutlinePage page;
	private final SapRequestObjectLinker linker;
	private final IPreferenceStore preferenceStore;
	
	@Inject
	public VCMLOutlineAction(IPreferenceStore preferenceStore, IResourceFactory resourceFactory, IContentOutlinePage outlinePage, SapRequestObjectLinker linker) {
		actionHandlers = new HashMap<String, IVCMLOutlineActionHandler<?>>();
		selectedObjects = new ArrayList<EObject>();
		
		CMConsolePlugin consolePlugin = CMConsolePlugin.getDefault();
		//out = new PrintStream(consolePlugin.getConsole(Kind.Task));
		result = new PrintStream(consolePlugin.getConsole(Kind.Result));
		err = new PrintStream(consolePlugin.getConsole(Kind.Error));
		
		this.page = outlinePage;
		this.linker = linker;
		this.resourceFactory = resourceFactory;
		this.preferenceStore = preferenceStore;
	}

	@Override
	public void run() {
		XtextResourceSet set = new XtextResourceSet();
		Resource usedResource = null;
		final boolean outputToFile = preferenceStore.getBoolean(IUiConstants.OUTPUT_TO_FILE);
		
		IXtextDocument document = null;
		Resource resource = null;
		
		if(outputToFile) {
			selectedObjects.clear();
			ISelection selection = page.getSelection();
			if(selection instanceof IStructuredSelection) {
				Iterator<?> iterator = ((IStructuredSelection)selection).iterator();
				while(iterator.hasNext()) {
					Object next = iterator.next();
					if(next instanceof EObjectNode) {
						EObjectNode objectNode = (EObjectNode)next;
						if(document == null) {
							document = objectNode.getDocument();
						}
						if(resource == null) {
							EObject root = document.readOnly(new IUnitOfWork<EObject, XtextResource>() {
								public EObject exec(XtextResource resource) throws Exception {
									return resource.getParseResult().getRootASTElement();
								}
							});
							resource = root.eResource();
						}
						selectedObjects.add(objectNode.getEObject(resource));
					}
				}
			}
		
			URI uri = resource.getURI();
			String platformString = uri.toPlatformString(true);
			String extension = "." + uri.fileExtension();
			String results = platformString.substring(0, platformString.lastIndexOf(extension)) + "_results_" + extension;
			IResource re = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(results));
			if(re instanceof IFile) {
				IFile file = (IFile)re; 
				if(!file.exists()) {
					try {
						file.create(new ByteArrayInputStream("".getBytes()), true, new NullProgressMonitor());
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				usedResource = set.createResource(URI.createURI(file.getLocationURI().toString()));
			}
		} else {
			usedResource = resourceFactory.createResource(URI.createURI("results"));
			set.getResources().add(usedResource);
		}
		if(usedResource != null) {
			final Resource res = usedResource;
			final Model vcmlModel = VCML.createModel();
			res.getContents().add(vcmlModel);
			
			Job job = new Job(getDescription()) {
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("Extracting objects from SAP system", IProgressMonitor.UNKNOWN);
					
					for(EObject obj : selectedObjects) {
						IVCMLOutlineActionHandler<?> actionHandler = actionHandlers.get(getInstanceTypeName(obj));
						if (actionHandler != null) {
							try {
								Method method = actionHandler.getClass().getMethod("run", new Class[]{getInstanceType(obj), Resource.class, IProgressMonitor.class});
								method.invoke(actionHandler, new Object[]{obj, res, monitor});
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
								// ignore 
							} catch (IllegalAccessException e) {
								e.printStackTrace();
								// ignore 
							} catch (InvocationTargetException e) {
								Throwable targetException = e.getTargetException();
								if (targetException instanceof OutlineActionCanceledException) {
									err.println("// canceled");
									break;
								} else {
									e.printStackTrace();
									targetException.printStackTrace(err); // display original cause in VClipse console
								}
							} catch (SecurityException e) {
								e.printStackTrace();
								// ignore 
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
								// ignore 
							} catch (Exception e) {
								e.printStackTrace(err); // this can be a JCoException or an AbapExeption
							}
						}
					}
					if(linker != null) {
						linker.linkModel(vcmlModel, new IDiagnosticConsumer() {
							public void consume(Diagnostic diagnostic, Severity severity) {
							}
							public boolean hasConsumedDiagnostics(Severity severity) {
								return false;
							}
						});
					}

					try {
						if(outputToFile) {
							res.save(null);
						} else {
							if (!vcmlModel.getObjects().isEmpty()) {
								result.println(((XtextResource)res).getSerializer().serialize(vcmlModel));
							}
							res.delete(null);
						}
					} catch (Exception e) {
						// currently, there can be exceptions if objects are not completeley initialized or linking fails
						// or IOExceptions
						e.printStackTrace(err);
					}
					
					IFile file = ResourceUtil.getFile(res);
					if(file != null && file.isAccessible()) {
						try {
							file.refreshLocal(IResource.DEPTH_ONE, monitor);
						} catch(CoreException exception) {
							exception.printStackTrace();
						}
					}
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.LONG);
			job.schedule();
		}
	}

	public void addHandler(String type, IVCMLOutlineActionHandler<?> handler) {
		if(handler != null) {
			actionHandlers.put(type, handler);
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if(selection instanceof TreeSelection) {
			boolean enabled = true;
			boolean visitedSomeAction = false;
			Iterator<?> iterator = ((TreeSelection)selection).iterator();
			if(enabled && iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof EObjectNode) {
					try {
						EObject eobject = ((EObjectNode)object).readOnly(new IUnitOfWork<EObject, EObject>() {
							public EObject exec(EObject eobject) throws Exception {
								return eobject;
							}
						});
						if(eobject != null) {
							IVCMLOutlineActionHandler<?> actionHandler = actionHandlers.get(getInstanceTypeName(eobject));
							if(actionHandler!=null) {
								visitedSomeAction = true;
								Method method = actionHandler.getClass().getMethod("isEnabled", new Class[]{getInstanceType(eobject)});
								enabled &= (Boolean)method.invoke(actionHandler, eobject);
							} else {
								enabled = false;
							}
						} else {
							enabled = false;
						}
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
						enabled = false;
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						enabled = false;
					} catch (InvocationTargetException e) {
						e.printStackTrace();
						enabled = false;
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						enabled = false;
					}
					setEnabled(visitedSomeAction && enabled);
				}
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
