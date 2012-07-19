package org.vclipse.idoc2jcoidoc.actions;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor;
import org.vclipse.idoc2jcoidoc.RFCIDocsSender;
import org.vclipse.vcml.vcml.VcmlModel;
import org.vclipse.vcml2idoc.transformation.VCML2IDocSwitch;

import com.google.inject.Inject;
import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.jco.JCoException;

/**
 *
 */
public class ConvertToIDocsAndSendAction implements IObjectActionDelegate {
	
	@Inject
	private IIDoc2JCoIDocProcessor idocProcessor;
	
	@Inject
	private VCML2IDocSwitch vcml2IDoc;
	
	@Inject
	private IConnectionHandler connectionHandler;
	
	private IStructuredSelection selection;
	
	public void run(IAction action) {
		Job job = new Job("Convert to IDocs and send to SAP system.") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Convert to IDocs and send to SAP system.", 4);
				IFile file = (IFile)selection.getFirstElement();
				monitor.subTask("Parsing VCML file " + file.getName());
				XtextResourceSet xtextResourceSet = new XtextResourceSet();
				URI uri = URI.createURI(file.getFullPath().toString());
				Resource resource = xtextResourceSet.getResource(uri, true);
				monitor.worked(1);
				EList<EObject> contents = resource.getContents();
				if(!contents.isEmpty()) {
					EObject object = contents.get(0);
					if(object instanceof VcmlModel) {
						VcmlModel vcmlModel = (VcmlModel)object;
						try {
							monitor.subTask("Converting VCML model to IDoc model...");
							org.vclipse.idoc.iDoc.Model idocModel = vcml2IDoc.vcml2IDocs(vcmlModel);
							monitor.worked(1);
							monitor.subTask("Converting IDoc model in JCo IDocs...");
							List<IDocDocument> idocs = idocProcessor.transform(idocModel , monitor);
							monitor.worked(1);
							monitor.subTask("Sending IDocs to SAP...");
							IStatus sendStatus = new RFCIDocsSender().send(idocs, connectionHandler, monitor);
							monitor.worked(1);
							monitor.done();
							return sendStatus;
						} catch (JCoException exception) {
							exception.printStackTrace();
						} catch (CoreException exception) {
							exception.printStackTrace();
						}
					}
				}
				return Status.OK_STATUS;
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
