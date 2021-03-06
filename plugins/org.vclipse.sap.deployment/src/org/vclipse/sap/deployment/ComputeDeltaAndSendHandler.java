/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.sap.deployment;

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.tigris.subversion.subclipse.core.SVNTeamProvider;
import org.vclipse.base.ui.FileListHandler;
import org.vclipse.idoc2jcoidoc.IDocSenderStatus;
import org.vclipse.sap.deployment.preferences.PreferencesInitializer;
import org.vclipse.vcml.compare.VCMLCompareOperation;
import org.vclipse.vcml.vcml.VCObject;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class ComputeDeltaAndSendHandler extends FileListHandler {

	private static final String SAP_CONTAINER = "SAP";
	
	@Inject
	private VCMLCompareOperation vcmlCompare;
	
	@Inject
	private OneClickWorkflow workflow;
	
	@Inject
	private IPreferenceStore preferenceStore;
	
	private IWorkbenchPart activePart;
	
	@Override
	public void handleListVariable(final Iterable<IFile> collection, ExecutionEvent event) {
		activePart = HandlerUtil.getActivePart(event);
		Job deltaDeploymentJob = new Job("Delta deployment job.") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final String errorMessage = "Error during delta extraction and deployment to SAP system";
				monitor.beginTask("Extracting delta and deploying to SAP system...", IProgressMonitor.UNKNOWN);
				Iterator<IFile> iterator = collection.iterator();
				if(iterator.hasNext()) {
					IFile currentFile = iterator.next();
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
						String path = currentFile.getFullPath().toString();
						URI uri = URI.createPlatformResourceURI(path, true);
						Resource newStateResource = resourceSet.getResource(uri, true);
						
						path = sapStateFile.getFullPath().toString();
						uri = URI.createPlatformResourceURI(path, true);
						Resource sapStateResource = resourceSet.getResource(uri, true);
						
						path = resultFile.getFullPath().toString();
						uri = URI.createPlatformResourceURI(path, true);
						Resource diffResource = resourceSet.getResource(uri, true);
						
						monitor.subTask("Comparing existing vcml resources.");
						vcmlCompare.compare(sapStateResource, newStateResource, diffResource, monitor);
						
						// the diff resource is empty
						EList<EObject> contents = diffResource.getContents();
						if(contents.isEmpty()) {
							Iterator<VCObject> filteredContent = Iterables.filter(contents.get(0).eContents(), VCObject.class).iterator();
							if(!filteredContent.hasNext()) {
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
								vcmlCompare.createMarkers(diffResource);
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
							sapStateFile.setContents(currentFile.getContents(), true, true, monitor);
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
					} catch(Exception exception) {
						DeploymentPlugin.showErrorDialog(errorMessage, exception.getMessage(), Status.CANCEL_STATUS);
						return Status.CANCEL_STATUS;
					}
				}
				return Status.OK_STATUS;
			}
		};
		deltaDeploymentJob.schedule();
	}
}
