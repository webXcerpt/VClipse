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
public class ExportInSapXmlAction implements IObjectActionDelegate {
	
	@Inject
	private IIDoc2JCoIDocProcessor idocProcessor;
	
	private IStructuredSelection selection;
	
	public void run(IAction action) {
		Job job = new Job("Creating a zip file.") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Creating zip file with idocs in SAP XML format...", IProgressMonitor.UNKNOWN);
				IFile file = (IFile)selection.getFirstElement();
				monitor.subTask("Loading idoc file " + file.getName());
				EList<EObject> contents = new XtextResourceSet().getResource(URI.createURI(file.getLocationURI().toString()), true).getContents();
				IFile zipIFile = file.getParent().getFile(new Path(file.getName().concat(".zip")));
				if(!contents.isEmpty()) {
					try {
						monitor.subTask("Converting idocs in jco idocs...");
						if(!monitor.isCanceled()) {
							List<IDocDocument> transform = idocProcessor.transform((Model)contents.get(0), monitor);
							monitor.worked(10);
							if(!transform.isEmpty()) {
								IDocXMLProcessor idocXmlProcessor = new DefaultIDocXMLProcessor(JCoIDoc.getIDocFactory());
								ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipIFile.getLocation().toString()));
								if(!zipIFile.isAccessible()) {
									zipIFile.create(new StringInputStream("".toString()), true, monitor);
								} else {
									zipIFile.setContents(new StringInputStream("".toString()), IResource.KEEP_HISTORY, monitor);
								}
								for(IDocDocument idoc : transform) {
									String iDocType = idoc.getIDocType();
									ZipEntry zipEntry = new ZipEntry(iDocType.concat("." + transform.indexOf(idoc) + ".").concat(".xml"));
									zipEntry.setComment(iDocType);
									zipEntry.setExtra(new byte[0]);
									byte[] bytes = idocXmlProcessor.render(idoc).getBytes();
									if(bytes.length > 0) {
										zos.putNextEntry(zipEntry);
										zos.write(bytes, 0, bytes.length);
										zos.closeEntry();
									}
									monitor.worked(1);
								}
								zos.close();
								zipIFile.refreshLocal(IResource.DEPTH_ONE, monitor);
							}
						}
					} catch (JCoException e) {
						e.printStackTrace();
						return Status.CANCEL_STATUS;
					} catch (CoreException e) {
						e.printStackTrace();
						return Status.CANCEL_STATUS;
					} catch (ZipException e) {
						e.printStackTrace();
						return Status.CANCEL_STATUS;
					} catch (IOException e) {
						e.printStackTrace();
						return Status.CANCEL_STATUS;
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
