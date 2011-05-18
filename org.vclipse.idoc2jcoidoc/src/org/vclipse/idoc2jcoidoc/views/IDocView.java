/**
 * Copyright � 2008, 2010 webXcerpt Software GmbH.
 * All rights reserved.
 *  
 * Contributor :
 *               webXcerpt Software GmbH
 */
package org.vclipse.idoc2jcoidoc.views;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.idoc.iDoc.Model;
import org.vclipse.idoc2jcoidoc.Activator;
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor;

import com.google.inject.Inject;
import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.jco.JCoException;

/**
 *
 */
public class IDocView extends ViewPart implements IShowInTarget {

	/**
	 * 
	 */
	public static final String ID = Activator.ID + ".views.idocview";
	
	/**
	 * 
	 */
	protected class IDocViewInput {
		
		private List<IDocDocument> idocs;
		
		public IDocViewInput(List<IDocDocument> idocs) {
			this.idocs = idocs;
		}
		
		public List<IDocDocument> getDocuments() {
			return idocs;
		}
	}
	
	/**
	 * 
	 */
	private class ResourceChangeListener implements IResourceChangeListener {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			try {
				event.getDelta().accept(new IResourceDeltaVisitor() {
					@Override
					public boolean visit(IResourceDelta delta) throws CoreException {
						IResource resource = delta.getResource();
						if(resource instanceof IFile && resource.equals(currentResource)) {
							IFile file = (IFile)resource;
							if("idoc".equals(file.getFileExtension())) {
								IDocView.this.setInput(file);
								return false;
							}
						}
						return true;
					}
				});
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 */
	private TreeViewer treeViewer;
	
	/**
	 * 
	 */
	private ResourceChangeListener resourceListener;
	
	/**
	 * 
	 */
	private IFile currentResource;
	
	/**
	 * 
	 */
	private SendIDocsAction action;
	
	/**
	 * 
	 */
	private final IIDoc2JCoIDocProcessor transformationProcessor;
	
	/**
	 * 
	 */
	private final IPreferenceStore preferenceStore;
	
	/**
	 * 
	 */
	private final IConnectionHandler handler;
	
	/**
	 * @param idoc2JCoIDocProcessor
	 */
	@Inject
	public IDocView(IIDoc2JCoIDocProcessor idoc2JCoIDocProcessor, IPreferenceStore preferenceStore, IConnectionHandler connectionHandler) {
		this.transformationProcessor = idoc2JCoIDocProcessor;
		this.preferenceStore = preferenceStore;
		this.handler = connectionHandler;
	}
	
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		getViewSite().setSelectionProvider(null);
		if(resourceListener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
		}
		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.part.PageBookView#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		treeViewer.setContentProvider(new IDocContentProvider());
		treeViewer.setLabelProvider(new IDocLabelProvider());
		action = new SendIDocsAction(treeViewer, handler, preferenceStore);
		action.setEnabled(false);
		IViewSite viewSite = getViewSite();
		viewSite.setSelectionProvider(treeViewer);
		viewSite.getActionBars().getToolBarManager().add(action);
	}

	/**
	 * @see org.eclipse.ui.part.PageBookView#setFocus()
	 */
	@Override
	public void setFocus() {
		if(treeViewer != null) {
			treeViewer.getTree().setFocus();
		}
	}
	
	/**
	 * @see org.eclipse.ui.part.IShowInTarget#show(org.eclipse.ui.part.ShowInContext)
	 */
	@Override
	public boolean show(ShowInContext context) {
		Object input = context.getInput();
		if(input instanceof IFileEditorInput) {
			setInput(((IFileEditorInput)input).getFile());
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 */
	public void setInput(IFile inputFile) {
		try {
			ResourceSet set = new XtextResourceSet();
			URI uri = URI.createURI(inputFile.getLocationURI().toString());
			Resource resource = set.createResource(uri);
			resource.load(null);
			EList<EObject> contents = resource.getContents();
			if(!contents.isEmpty()) {
				EObject eobject = contents.get(0);
				if(eobject instanceof Model) {
					final Model model = (Model)eobject;
					// remove old/add new resource change handler
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					if(resourceListener != null) {
						workspace.removeResourceChangeListener(resourceListener);
					}
					resourceListener = new ResourceChangeListener();
					workspace.addResourceChangeListener(resourceListener, IResourceChangeEvent.POST_CHANGE);
					currentResource = inputFile;
					
					// start the transformation
					Job job = new Job("Converting to JCo IDocs...") {
						@Override
						protected IStatus run(IProgressMonitor monitor) {
							try {
								final List<IDocDocument> idocs = transformationProcessor.transform(model, monitor);
								Display.getDefault().syncExec(new Runnable() {
									@Override
									public void run() {
										treeViewer.setInput(new IDocViewInput(idocs));
										action.setEnabled(!idocs.isEmpty());
									}
								});
								return Status.OK_STATUS;
							} catch (final CoreException exception) {
								Display.getDefault().syncExec(new Runnable() {
									@Override
									public void run() {
										ErrorDialog.openError(getSite().getShell(), 
												"Transformation error", "Error during IDoc to JCo IDoc transformation.", exception.getStatus());
									}
								});
								return Status.CANCEL_STATUS;
							} catch (final JCoException exception) {
								Display.getDefault().syncExec(new Runnable() {
									@Override
									public void run() {
										ErrorDialog.openError(getSite().getShell(), 
												"Transformation error", "Error during IDoc to JCo IDoc transformation.", 
												new Status(IStatus.ERROR, VClipseConnectionPlugin.ID, exception.getMessage()));
									}
								});
								return Status.CANCEL_STATUS;
							}
						}
					};
					job.setPriority(Job.SHORT);
					job.schedule();
				}
			}
		} catch (IOException e) {
			Activator.log(e.getMessage(), e);
		}
	}
}
