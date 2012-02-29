package org.vclipse.sap.deployment;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.eclipse.xtext.util.StringInputStream;
import org.tigris.subversion.subclipse.core.SVNTeamProvider;
import org.vclipse.idoc2jcoidoc.IDocSenderStatus;
import org.vclipse.sap.deployment.preferences.PreferencesInitializer;
import org.vclipse.vcml.diff.Comparison;
import org.vclipse.vcml.vcml.VCObject;

import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class DeltaDeploymentAction implements IObjectActionDelegate {

	private static final String SAP_CONTAINER = "SAP";
	
	@Inject
	private Comparison comparison;
	
	@Inject
	private OneClickWorkflow workflow;
	
	@Inject
	private IPreferenceStore preferenceStore;
	
	private IStructuredSelection selection;
	
	@Override
	public void run(IAction action) {
		Job deltaDeploymentJob = new Job("Delta deployment job") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask("Extracting delta and deploying to SAP system...", IProgressMonitor.UNKNOWN);
				Object firstElement = selection.getFirstElement();
				if(firstElement instanceof IFile) {
					IFile file = (IFile)firstElement;
					XtextResourceSet resourceSet = new XtextResourceSet();
					try {
						IFolder folder = file.getProject().getFolder(new Path(SAP_CONTAINER));
						if(!folder.exists()) {
							folder.create(true, true, monitor);
						}
						IFile sapStateFile = folder.getFile(file.getName());
						if(!sapStateFile.exists()) {
							sapStateFile.create(new StringInputStream(""), true, monitor);
							folder.refreshLocal(IResource.DEPTH_ONE, monitor);
						}
						IFile resultFile = folder.getFile(file.getName().replace(".vcml", ".diff.vcml"));

						Resource fileResource = resourceSet.getResource(URI.createURI(file.getLocationURI().toString()), true);
						Resource sapStateResource = resourceSet.getResource(URI.createURI(sapStateFile.getLocationURI().toString()), true);

						Resource diffResource = resourceSet.createResource(URI.createURI(resultFile.getLocationURI().toString()));
						monitor.subTask("Comparing models");
						comparison.compare(sapStateResource, fileResource, diffResource, monitor);
						
						Iterator<VCObject> iterator = Iterables.filter(diffResource.getContents().get(0).eContents(), VCObject.class).iterator();
						if(!iterator.hasNext()) {
							final Display display = Display.getDefault();
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									ErrorDialog.openError(display.getActiveShell(), "Error during deployment to SAP system", 
											"Delta deployment to SAP system was cancelled.", new Status(IStatus.ERROR, DeploymentPlugin.ID, "No differing objects found"));									
								}
							});
							monitor.done();
							return Status.CANCEL_STATUS;
						}
						
						monitor.subTask("Converting to IDocs and sending to SAP");
						if(preferenceStore.getBoolean(PreferencesInitializer.SAVE_DIFF_FILES)) {
							diffResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());											
						}
						monitor.done();
						IStatus status = workflow.convertAndSend(diffResource, monitor);
						if(status.isOK()) {
							Files.copy(new File(fileResource.getURI().toFileString()), new File(sapStateResource.getURI().toFileString()));
							if(preferenceStore.getBoolean(PreferencesInitializer.EXECUTE_SVN_COMMIT)) {
								sapStateFile = ResourceUtil.getFile(sapStateResource);
								SVNTeamProvider teamProvider = new SVNTeamProvider();
								teamProvider.setProject(sapStateFile.getProject());
								String commitMessage = "New state of VCML product model.";
								if(status instanceof IDocSenderStatus) {
									IDocSenderStatus senderStatus = (IDocSenderStatus)status;
									commitMessage += " Sent to " + senderStatus.getSapSystem() + " with UPS number " + senderStatus.getUpsNumber();
								}
								teamProvider.checkin(new IResource[]{sapStateFile}, commitMessage, false, IResource.DEPTH_ONE, monitor);
							}
						}
						folder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
						return status;
					} catch (InterruptedException exception) {
						exception.printStackTrace();
						return Status.CANCEL_STATUS;
					} catch (IOException exception) {
						exception.printStackTrace();
						return Status.CANCEL_STATUS;
					} catch (JCoException exception) {
						exception.printStackTrace();
						return Status.CANCEL_STATUS;
					} catch (CoreException exception) {
						exception.printStackTrace();
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
		// not used
	}
}

