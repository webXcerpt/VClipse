package org.vclipse.sap.deployment;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xtext.resource.SaveOptions;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.idoc.iDoc.IDoc;
import org.vclipse.idoc.iDoc.IDocFactory;
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor;
import org.vclipse.idoc2jcoidoc.RFCIDocsSender;
import org.vclipse.sap.deployment.preferences.PreferencesInitializer;
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
	
	@Inject
	private IPreferenceStore preferenceStore;
	
	public IStatus convertAndSend(Resource resource, IProgressMonitor monitor) throws JCoException, CoreException, IOException {
		EList<EObject> contents = resource.getContents();
		if(!contents.isEmpty()) {
			EObject object = contents.get(0);
			org.vclipse.idoc.iDoc.Model idocModel = IDocFactory.eINSTANCE.createModel();
			if(object instanceof Model) {
				monitor.subTask("Converting VCML model to IDoc model...");
				idocModel = vcml2IDoc.vcml2IDocs((Model)object);
				monitor.worked(1);
			} else if(object instanceof org.vclipse.idoc.iDoc.Model) {
				idocModel = (org.vclipse.idoc.iDoc.Model)object;
			}
			
			boolean onlyUpsMas = true;
			for(IDoc currentIDoc : idocModel.getIdocs()) {
				String messageType = currentIDoc.getMessageType();
				if(!"UPSMAS".equals(messageType)) {
					onlyUpsMas = false;
				}
			}
			if(onlyUpsMas) {
				final Display display = Display.getDefault();
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						ErrorDialog.openError(
								display.getActiveShell(), "Error during SAP deployment", 
									"IDoc deployment to SAP system was cancelled.", new Status(IStatus.ERROR, DeploymentPlugin.ID, "No IDocs generated"));
					}
				});
				monitor.done();
				return Status.CANCEL_STATUS;
			}
			
			monitor.subTask("Converting IDoc model in JCo IDocs...");
			List<IDocDocument> idocs = idocProcessor.transform(idocModel , monitor);
			if(preferenceStore.getBoolean(PreferencesInitializer.SAVE_IDOC_FILES)) {
				ResourceSet resourceSet = resource.getResourceSet();
				Resource idocResource = resourceSet.createResource(URI.createURI(resource.getURI().toString().concat(".idoc")));
				idocResource.getContents().add(idocModel);
				idocResource.save(SaveOptions.defaultOptions().toOptionsMap());
			}
			monitor.worked(1);
			monitor.subTask("Sending IDocs to SAP...");
			IStatus sendStatus = new RFCIDocsSender().send(idocs, connectionHandler, monitor);
			monitor.worked(1);
			monitor.done();
			return sendStatus;
		}
		return Status.OK_STATUS;
	}
}
