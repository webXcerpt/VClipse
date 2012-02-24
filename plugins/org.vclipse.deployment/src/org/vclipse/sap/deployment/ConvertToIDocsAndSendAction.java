package org.vclipse.sap.deployment;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

/**
 *
 */
public class ConvertToIDocsAndSendAction implements IObjectActionDelegate {
	
	@Inject
	private OneClickWorkflow workflow;
	
	private IStructuredSelection selection;
	
	public void run(IAction action) {
		Job job = new Job("Convert to IDocs and send to SAP system.") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Convert to IDocs and send to SAP system.", 4);
				IFile file = (IFile)selection.getFirstElement();
				monitor.subTask("Parsing VCML file " + file.getName());
				XtextResourceSet xtextResourceSet = new XtextResourceSet();
				Resource resource = xtextResourceSet.getResource(URI.createURI(file.getLocationURI().toString()), true);
				monitor.worked(1);
				try {
					return workflow.convertAndSend(resource, monitor);
				} catch (JCoException exception) {
					exception.printStackTrace();
					return Status.CANCEL_STATUS;
				} catch (CoreException exception) {
					exception.printStackTrace();
					return Status.CANCEL_STATUS;
				}
			}
		};
		job.schedule();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = (IStructuredSelection)selection;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
}
