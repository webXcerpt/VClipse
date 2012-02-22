package org.vclipse.idoc2jcoidoc.actions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.StringInputStream;
import org.vclipse.idoc.iDoc.Model;
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor;

import com.google.inject.Inject;
import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocXMLProcessor;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.idoc.rt.xml.DefaultIDocXMLProcessor;
import com.sap.conn.jco.JCoException;

/**
 *
 */
public class ConvertToIDocsAndSendAction implements IObjectActionDelegate {
	
	@Inject
	private IIDoc2JCoIDocProcessor idocProcessor;
	
	private IStructuredSelection selection;
	
	public void run(IAction action) {
		Job job = new Job("Convert to IDocs and send.") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Convert to IDocs and send.", IProgressMonitor.UNKNOWN);
				IFile file = (IFile)selection.getFirstElement();
				monitor.subTask("Converting to idocs..." + file.getName());
				monitor.subTask("Converting idocs in jco idocs...");
				monitor.subTask("Sending to SAP...");
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
