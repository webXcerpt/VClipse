package org.vclipse.sap.deployment;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.vclipse.vcml.diff.Comparison;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;
import com.webxcerpt.cm.nsn.cml.generator.CmlOutputConfigurationProvider;

public class DeltaDeploymentAction implements IObjectActionDelegate {

	@Inject
	private IOutputConfigurationProvider outputConfigurationProvider;
	
	@Inject
	private Comparison comparison;
	
	@Inject
	private OneClickWorkflow workflow;
	
	private IStructuredSelection selection;
	
	@Override
	public void run(IAction action) {
		Job deltaDeploymentJob = new Job("Delta deployment job") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask("Extracting delta and deploying to SAP system...", IProgressMonitor.UNKNOWN);
				if(selection != null) {
					Object firstElement = selection.getFirstElement();
					if(firstElement instanceof IFile) {
						IFile file = (IFile)firstElement;
						XtextResourceSet resource = new XtextResourceSet();
						Iterator<OutputConfiguration> outputConfigurations = outputConfigurationProvider.getOutputConfigurations().iterator();
						while(outputConfigurations.hasNext()) {
							OutputConfiguration configuration = outputConfigurations.next();
							if(CmlOutputConfigurationProvider.SAP_STATE_OUTPUT.equals(configuration.getName())) {
								IFolder folder = file.getProject().getFolder(new Path(configuration.getOutputDirectory().replace("./", "")));
								IFile sapStateFile = folder.getFile(file.getName());
								if(!sapStateFile.exists()) {
									break;
								} else {
									IFile resultFile = folder.getFile(file.getName().replace(".vcml", ".diff.vcml"));
									Resource fileResource = resource.getResource(URI.createURI(file.getLocationURI().toString()), true);
									Resource sapStateResource = resource.getResource(URI.createURI(sapStateFile.getLocationURI().toString()), true);
									
									// TODO add an option for save
									
									try {
										Resource resultResource = resource.createResource(URI.createURI(resultFile.getLocationURI().toString()));
										monitor.subTask("Comparing models");
										comparison.compare(sapStateResource, fileResource, resultResource, monitor);
										monitor.subTask("Converting to IDocs and sending to SAP");
										IStatus status =  workflow.convertAndSend(resultResource, monitor);
										resultResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
										monitor.done();
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
							}
						}
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

