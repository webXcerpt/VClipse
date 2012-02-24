package org.vclipse.sap.deployment;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor;
import org.vclipse.idoc2jcoidoc.RFCIDocsSender;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml2idoc.builder.VCML2IDocSwitch;

import com.google.inject.Inject;
import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.jco.JCoException;

public class OneClickWorkflow {

	@Inject
	private IIDoc2JCoIDocProcessor idocProcessor;
	
	@Inject
	private VCML2IDocSwitch vcml2IDoc;
	
	@Inject
	private IConnectionHandler connectionHandler;
	
	public IStatus convertAndSend(Resource resource, IProgressMonitor monitor) throws JCoException, CoreException {
		EList<EObject> contents = resource.getContents();
		if(!contents.isEmpty()) {
			EObject object = contents.get(0);
			if(object instanceof Model) {
				monitor.subTask("Converting VCML model to IDoc model...");
				org.vclipse.idoc.iDoc.Model idocModel = vcml2IDoc.vcml2IDocs((Model)object);
				monitor.worked(1);
				monitor.subTask("Converting IDoc model in JCo IDocs...");
				List<IDocDocument> idocs = idocProcessor.transform(idocModel , monitor);
				monitor.worked(1);
				monitor.subTask("Sending IDocs to SAP...");
				IStatus sendStatus = new RFCIDocsSender().send(idocs, connectionHandler, monitor);
				monitor.worked(1);
				monitor.done();
				return sendStatus;
			}
		}
		return Status.OK_STATUS;
	}
}
