package org.vclipse.sap.deployment;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.tigris.subversion.subclipse.core.SVNTeamProvider;
import org.vclipse.base.ui.util.VClipseResourceUtil;
import org.vclipse.idoc2jcoidoc.IDocSenderStatus;
import org.vclipse.sap.deployment.preferences.PreferencesInitializer;
import org.vclipse.vcml.diff.compare.VcmlCompare;
import org.vclipse.vcml.vcml.VCObject;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class DeltaDeploymentAction implements IObjectActionDelegate {

	private static final String SAP_CONTAINER = "SAP";
	
	@Inject
	private VcmlCompare vcmlCompare;
	
	@Inject
	private OneClickWorkflow workflow;
	
	@Inject
	private IPreferenceStore preferenceStore;
	
	@Inject
	private VClipseResourceUtil resourceUtils;
	
	private IStructuredSelection selection;
	private IWorkbenchPart activePart;
	
	@Override
	public void run(IAction action) {
		Job deltaDeploymentJob = new Job("Delta deployment job.") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final String errorMessage = "Error during delta extraction and deployment to SAP system";
				monitor.beginTask("Extracting delta and deploying to SAP system...", IProgressMonitor.UNKNOWN);
				Object firstElement = selection.getFirstElement();
				if(firstElement instanceof IFile) {
					IFile currentFile = (IFile)firstElement;
					XtextResourceSet resourceSet = new XtextResourceSet();
					try {
						
						// handle files and folders
						IFolder folder = currentFile.getProject().getFolder(new Path(SAP_CONTAINER));
						if(!folder.exists()) {
							folder.create(true, true, monitor);
						}
						IFile sapStateFile = folder.getFile(currentFile.getName());
						if(!sapStateFile.exists()) {
							sapStateFile.create(new StringInputStream(""), true, monitor);
							folder.refreshLocal(IResource.DEPTH_ONE, monitor);
						}
						final IFile resultFile = folder.getFile(currentFile.getName().replace(".vcml", ".diff.vcml"));
						if(!resultFile.exists())  {
							resultFile.create(new StringInputStream(""), true, monitor);
						}

						// handle resources
						Resource newStateResource = resourceUtils.getResource(resourceSet, currentFile);
						Resource sapStateResource = resourceUtils.getResource(resourceSet, sapStateFile);
						Resource diffResource = resourceUtils.getResource(resourceSet, resultFile);
						
						monitor.subTask("Comparing existing vcml resources.");
						vcmlCompare.compare(sapStateResource, newStateResource, diffResource, monitor);
						
						// the diff resource is empty
						EList<EObject> contents = diffResource.getContents();
						if(contents.isEmpty()) {
							Iterator<VCObject> iterator = Iterables.filter(contents.get(0).eContents(), VCObject.class).iterator();
							if(!iterator.hasNext()) {
								DeploymentPlugin.showErrorDialog("Error during deployment to SAP system", 
										"Delta deployment to SAP system was cancelled.", 
											new Status(IStatus.ERROR, DeploymentPlugin.ID, "No differing objects found"));
								monitor.done();
								return Status.CANCEL_STATUS;
							}
						}
						
						monitor.subTask("Saving the diff file.");
						if(preferenceStore.getBoolean(PreferencesInitializer.SAVE_DIFF_FILES)) {
							diffResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());	
							if(vcmlCompare.reportedProblems()) {
								vcmlCompare.createMarkers(resultFile, diffResource);
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										try {
											IWorkbenchPartSite site = activePart.getSite();
											if(site != null) {
												IEditorPart editorPart = IDE.openEditor(site.getPage(), resultFile);
												if(editorPart instanceof XtextEditor) {
													((XtextEditor)editorPart).getDocument().modify(new IUnitOfWork<XtextResource, XtextResource>() {
														@Override
														public XtextResource exec(XtextResource state)throws Exception {
															state.setModified(true);
															state.save(SaveOptions.defaultOptions().toOptionsMap());
															return state;
														}
													});
												}												
											}
										} catch (PartInitException exception) {
											DeploymentPlugin.showErrorDialog(errorMessage, exception.getMessage(), Status.CANCEL_STATUS);
										}									
									}
								});
								DeploymentPlugin.showErrorDialog(errorMessage, 
										"Found changes in VCObject that are not allowed for execution in SAP system." +
										"Please look into the diff file for more information.", Status.CANCEL_STATUS);
								return Status.CANCEL_STATUS;
							}
						}
						
						monitor.subTask("Converting to IDocs and sending to SAP");
						IStatus status = workflow.convertAndSend(diffResource, monitor);
						if(status.isOK()) {
							sapStateFile.delete(true, monitor);
							currentFile.copy(sapStateFile.getFullPath(), true, monitor);
							if(preferenceStore.getBoolean(PreferencesInitializer.EXECUTE_SVN_COMMIT)) {
								// sapStateFile = ResourceUtil.getFile(sapStateResource);
								SVNTeamProvider teamProvider = new SVNTeamProvider();
								IProject project = sapStateFile.getProject();
								teamProvider.setProject(project);
								String commitMessage = "New state of VCML product model.";
								if(status instanceof IDocSenderStatus) {
									IDocSenderStatus senderStatus = (IDocSenderStatus)status;
									commitMessage += " Sent to " + senderStatus.getSapSystem() + " with UPS number " + senderStatus.getUpsNumber();
								}
								teamProvider.add(new IResource[]{folder, sapStateFile}, IResource.DEPTH_ONE, monitor);
								teamProvider.checkin(new IResource[]{folder, sapStateFile}, commitMessage, false, IResource.DEPTH_ONE, monitor);
							}
						}
						if(!preferenceStore.getBoolean(PreferencesInitializer.SAVE_DIFF_FILES)) {
							resultFile.delete(true, monitor);
						}
						folder.refreshLocal(IResource.DEPTH_ONE, monitor);
						return Status.OK_STATUS;
					} catch (InterruptedException exception) {
						DeploymentPlugin.showErrorDialog(errorMessage, exception.getMessage(), Status.CANCEL_STATUS);
						return Status.CANCEL_STATUS;
					} catch (IOException exception) {
						DeploymentPlugin.showErrorDialog(errorMessage, exception.getMessage(), Status.CANCEL_STATUS);
						return Status.CANCEL_STATUS;
					} catch (CoreException exception) {
						DeploymentPlugin.showErrorDialog(errorMessage, exception.getMessage(), Status.CANCEL_STATUS);
						return Status.CANCEL_STATUS;
					} catch (JCoException exception) {
						DeploymentPlugin.showErrorDialog(errorMessage, exception.getMessage(), Status.CANCEL_STATUS);
						return Status.CANCEL_STATUS;
					}
				}
				return Status.OK_STATUS;
			}
		};
		deltaDeploymentJob.schedule();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection)selection;
		}
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		activePart = targetPart;
	}
}

