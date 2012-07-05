package org.vclipse.sap.deployment;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.vclipse.base.ui.FileListHandler;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class ConvertAndSendHandler extends FileListHandler {

	@Inject
	private OneClickWorkflow workflow;
	
	@Override
	public void handleListVariable(final Iterable<IFile> collection, ExecutionEvent event) {
		Job job = new Job("Convert to IDocs and send to SAP system.") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				String errorMessage = "Error during idoc deployment to SAP system.";
				monitor.beginTask("Converting and sending to SAP system.", 4);
				Iterator<IFile> iterator = collection.iterator();
				while(iterator.hasNext()) {
					IFile file = iterator.next();
					monitor.subTask("Parsing " + file.getFileExtension() + " file " + file.getName());
					URI uri = URI.createURI(file.getLocationURI().toString());
					Resource resource = new XtextResourceSet().getResource(uri, true);
					monitor.worked(1);
					try {
						return workflow.convertAndSend(resource, monitor);
					} catch (JCoException exception) {
						DeploymentPlugin.showErrorDialog(errorMessage, exception.getMessage(), Status.CANCEL_STATUS);
						continue;
					} catch (CoreException exception) {
						DeploymentPlugin.showErrorDialog(errorMessage, exception.getMessage(), Status.CANCEL_STATUS);
						continue;
					} catch (IOException exception) {
						DeploymentPlugin.showErrorDialog(errorMessage, exception.getMessage(), Status.CANCEL_STATUS);
						continue;
					}
				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}
