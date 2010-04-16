/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
package org.vclipse.vcml.outline.actions;

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
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.concurrent.IUnitOfWork;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer;
import org.eclipse.xtext.linking.ILinker;
import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.common.editor.outline.ContentOutlineNode;
import org.vclipse.console.CMConsolePlugin;
import org.vclipse.console.CMConsolePlugin.Kind;
import org.vclipse.vcml.IUiConstants;
import org.vclipse.vcml.VCMLUiPlugin;
import org.vclipse.vcml.outline.VCMLOutlinePage;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.VcmlFactory;


/**
 * 
 */
public class VCMLOutlineAction extends Action implements ISelectionChangedListener {

	
	private IResourceFactory resourceFactory;
	
	
	private ILinker linker;

	/**
	 * 
	 */
	private final Map<String,IVCMLOutlineActionHandler<?>> actionHandlers;
	
	/**
	 * 
	 */
	private final List<EObject> selectedObjects;
	
	protected static final VcmlFactory VCMLFACTORY = VcmlFactory.eINSTANCE;
	
	private VCMLOutlinePage page;
	
	protected PrintStream out; 
	protected PrintStream result; 
	protected PrintStream err;


	/**
	 * @param resourceFactory 
	 * @param vCMLOutlinePage 
	 * @param linkingService TODO
	 * @param linker2 
	 * 
	 */
	public VCMLOutlineAction(IResourceFactory resourceFactory, VCMLOutlinePage vCMLOutlinePage, ILinker linker, 
			ILinkingService linkingService, IGrammarAccess grammarAccess) {
		this.resourceFactory = resourceFactory;
		actionHandlers = new HashMap<String,IVCMLOutlineActionHandler<?>>();
		selectedObjects = new ArrayList<EObject>();
		page = vCMLOutlinePage;
		out = new PrintStream(CMConsolePlugin.getDefault().getConsole(Kind.Task));
		result = new PrintStream(CMConsolePlugin.getDefault().getConsole(Kind.Result));
		err = new PrintStream(CMConsolePlugin.getDefault().getConsole(Kind.Error));
		this.linker = linker;
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run() {
		final IPreferenceStore preferenceStore = VCMLUiPlugin.getInstance().getPreferenceStore();
		XtextResourceSet set = new XtextResourceSet();
		Resource usedResource = null;
		final boolean outputToFile = preferenceStore.getBoolean(IUiConstants.OUTPUT_TO_FILE);
		if(outputToFile) {
			Model model = page.getDocument().readOnly(new IUnitOfWork<Model, XtextResource>() {
				public Model exec(XtextResource resource) throws Exception {
					return (Model)resource.getParseResult().getRootASTElement();
				}
			});
			Resource resource = model.eResource();
			URI uri = resource.getURI();
			String platformString = uri.toPlatformString(true);
			String extension = "." + uri.fileExtension();
			String na = platformString.substring(0, platformString.lastIndexOf(extension)) + "_results_" + extension;
			IResource re = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(na));
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
			Job job = new Job(getDescription()) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("", IProgressMonitor.UNKNOWN);
					Model resultModel = VCMLFACTORY.createModel();
					res.getContents().add(resultModel);
					
					for(EObject obj : getSelectedObjects()) {
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
					if (linker!=null) {
						linker.linkModel(resultModel, new IDiagnosticConsumer() {

							public boolean hasConsumedDiagnostics() {
								// TODO Auto-generated method stub
								return false;
							}

							public void consume(Diagnostic diagnostic) {
								// TODO Auto-generated method stub
							}
						});
					}

					try {
						if(outputToFile) {
							res.save(null);
						} else {
							if (!resultModel.getObjects().isEmpty()) {
								result.println(((XtextResource)res).getSerializer().serialize(resultModel));
							}
							res.delete(null);
						}
					} catch (Exception e) {
						// currently, there can be exceptions if objects are not completeley initialized or linking fails
						// or IOExceptions
						e.printStackTrace(err);
					}
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.LONG);
			job.schedule();
		}
	}

	/**
	 * @param cls 
	 */
	public synchronized void addHandler(String cls, IVCMLOutlineActionHandler<?> handler) {
		if(handler != null) {
			actionHandlers.put(cls, handler);
		}
	}

	public synchronized void removeHandler(String cls) {
		actionHandlers.remove(cls);
	}

	/**
	 * @return
	 */
	private synchronized EObject[] getSelectedObjects() {
		return selectedObjects.toArray(new EObject[selectedObjects.size()]);
	}
	
	/**
	 * @param obj
	 */
	private synchronized void addSelectedObject(EObject obj) {
		selectedObjects.add(obj);
	}
	
	/**
	 * 
	 */
	private synchronized void removeSelectedObjects() {
		selectedObjects.clear();
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@SuppressWarnings("unchecked")
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		removeSelectedObjects();
		if(selection instanceof IStructuredSelection) {
			IStructuredSelection strSelection = (IStructuredSelection)selection;
			Iterator<ContentOutlineNode> iterator = strSelection.iterator(); // IStructuredSelection does not implement Iterable!
			boolean enabled = true;
			boolean visitedSomeAction = false;
			try {
			while(enabled && iterator.hasNext()) {
				ContentOutlineNode node = iterator.next();
				EObject obj = node.getEObjectHandle().readOnly(new IUnitOfWork<EObject, EObject>() {
					public EObject exec(EObject eobject) throws Exception {
						return eobject;
					}
				});
				IVCMLOutlineActionHandler<?> actionHandler = actionHandlers.get(getInstanceTypeName(obj));
				if (actionHandler!=null) {
					visitedSomeAction = true;
					Method method = actionHandler.getClass().getMethod("isEnabled", new Class[]{getInstanceType(obj)});
					enabled &= (Boolean)method.invoke(actionHandler, obj);
					if(enabled) {
						addSelectedObject(obj);
					}
				} else {
					enabled = false;
				}
			}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				removeSelectedObjects();
				enabled = false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				removeSelectedObjects();
				enabled = false;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				removeSelectedObjects();
				enabled = false;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				removeSelectedObjects();
				enabled = false;
			}

			setEnabled(visitedSomeAction && enabled);
		}
	}
	
	private Class<?> getInstanceType(EObject obj) throws ClassNotFoundException {
		return Class.forName(getInstanceTypeName(obj));
	}
	
	private String getInstanceTypeName(EObject obj) {
		return obj.eClass().getInstanceTypeName();
	}
}
