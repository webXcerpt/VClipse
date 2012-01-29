package org.vclipse.configscan.vcmlt.ui.action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.ITestObjectFilter;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.vcmlt.vcmlT.Model;
import org.vclipse.configscan.vcmlt.vcmlT.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.inject.Inject;

public class ConvertVcmlT2XmlAction implements IObjectActionDelegate {
	
	public static final String CONFIGSCAN_EXTENSION = "xml";

	private IStructuredSelection selection;
	
	@Inject 
	private IConfigScanXMLProvider configScanXMLProvider;
	
	@Inject
	private DocumentUtility documentUtility;
	
	@Inject
	private ITestObjectFilter filter;
	
	public void run(IAction action) {
		Job convertJob = new Job("VCMLT file to ConfigScan XML file") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Converting VCMLT file to ConfigScan XML file", IProgressMonitor.UNKNOWN);
				IFile firstElement = (IFile)selection.getFirstElement();
				Resource resource = new XtextResourceSet().getResource(URI.createURI(firstElement.getLocationURI().toString()), true);
				if (resource == null) {
					throw new IllegalArgumentException("resource null");
				}
				EList<EObject> contents = resource.getContents();
				if (contents.size()==0) {
					throw new IllegalArgumentException("no contents");
				}
				if (!(contents.get(0) instanceof Model)) {
					throw new IllegalArgumentException("no model");
				}
				Model model = (Model) contents.get(0);
				TestCase testcase = model.getTestcase();
				if (testcase==null) {
					throw new IllegalArgumentException("no testcase");
				}
				URI xmlFileExtension = resource.getURI().appendFileExtension(CONFIGSCAN_EXTENSION);
				try {
					OutputStream outputStream = new ExtensibleURIConverterImpl().createOutputStream(xmlFileExtension);
					Document doc = configScanXMLProvider.transform(model, filter, new HashMap<Element, URI>());
					String output = documentUtility.serialize(doc);
					outputStream.write(output.getBytes());
					outputStream.close();
					firstElement.getParent().refreshLocal(IResource.DEPTH_ONE, monitor);
				} catch (IOException e) {
					System.out.println(e.getMessage());
				} catch (CoreException e) {
					System.out.println(e.getMessage());
				}
				return Status.OK_STATUS;
			}
		};
		convertJob.setPriority(Job.BUILD);
		convertJob.schedule();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = (IStructuredSelection)selection;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}
}
