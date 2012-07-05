package org.vclipse.idoc2jcoidoc.actions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.commands.ExecutionEvent;
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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.StringInputStream;
import org.vclipse.base.ui.FileListHandler;
import org.vclipse.idoc.iDoc.Model;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor;

import com.google.inject.Inject;
import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocXMLProcessor;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.idoc.rt.xml.DefaultIDocXMLProcessor;
import com.sap.conn.jco.JCoException;

public class TransformIDoc2SapXmlHandler extends FileListHandler {

	@Inject
	private IIDoc2JCoIDocProcessor idocProcessor;
	
	@Override
	public void handleListVariable(final Iterable<IFile> collection, ExecutionEvent event) {
		Job job = new Job("Creating a zip file.") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Creating zip file with idocs in SAP XML format...", IProgressMonitor.UNKNOWN);
				Iterator<IFile> iterator = collection.iterator();
				if(!iterator.hasNext()) {
					return Status.CANCEL_STATUS;
				} else {
					IFile file = iterator.next();
					monitor.subTask("Loading idoc file " + file.getName());
					URI uri = URI.createURI(file.getFullPath().toString());
					Resource resource = new XtextResourceSet().getResource(uri, true);
					EList<EObject> contents = resource.getContents();
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
						} catch (JCoException exception) {
							IDoc2JCoIDocPlugin.log(exception.getMessage(), exception);
							return Status.CANCEL_STATUS;
						} catch (CoreException exception) {
							IDoc2JCoIDocPlugin.log(exception.getMessage(), exception);
							return Status.CANCEL_STATUS;
						} catch (ZipException exception) {
							IDoc2JCoIDocPlugin.log(exception.getMessage(), exception);
							return Status.CANCEL_STATUS;
						} catch (IOException exception) {
							IDoc2JCoIDocPlugin.log(exception.getMessage(), exception);
							return Status.CANCEL_STATUS;
						}
					}
					return Status.OK_STATUS;
				}
			}
		};
		job.schedule();
	}
}
