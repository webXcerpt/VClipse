package org.vclipse.configscan.vcmlt.ui.imports;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.StringInputStream;
import org.vclipse.configscan.vcmlt.imports.ConfigScanXml2VcmlTImport;
import org.vclipse.configscan.vcmlt.ui.internal.VcmlTActivator;
import org.vclipse.configscan.vcmlt.vcmlT.Model;
import org.vclipse.configscan.vcmlt.vcmlT.TestCase;
import org.vclipse.configscan.vcmlt.vcmlT.VcmlTFactory;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

public class ConfigScanXml2VcmlTImportWizard extends Wizard implements IImportWizard {

	private static final String ERROR_DIALOG_TITLE = "Error during VCMLT import";

	private static final String SYMBOLIC_NAME = VcmlTActivator.getInstance().getBundle().getSymbolicName();

	private static final VcmlTFactory VCMLT = VcmlTFactory.eINSTANCE;
	
	@Inject
	private ConfigScanXml2VcmlTImport configScanXml2VcmlTImport;
	
	private WizardPage page;
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		page = new WizardPage("", selection);
		setWindowTitle("Import wizard for VCMLT test cases from ConfigScan (*.xml) files");
		setNeedsProgressMonitor(true);
	}

	public void addPages() {
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Transforming ConfigScan XML input files to VCMLT files...", IProgressMonitor.UNKNOWN);

					IFile targetFile = page.getTargetFile();
					Resource newVcmlTResource = null;
					Model vcmltModel = null;

					try {
						if(targetFile.exists() && page.overwriteExistigTargetFile()) {
							targetFile.setContents(new StringInputStream(""), IResource.KEEP_HISTORY, monitor);
						} else {
							targetFile.create(new StringInputStream(""), true, monitor);
						}
					} catch (CoreException e) {
						ErrorDialog.openError(getShell(), ERROR_DIALOG_TITLE, "Can not create file " + targetFile.getName(), new Status(IStatus.ERROR, 
								SYMBOLIC_NAME, e.getMessage()));
					}

					org.vclipse.vcml.vcml.Model referencedModel = null;
					EList<EObject> contents = new XtextResourceSet().getResource(URI.createURI(page.getModelFile().getLocationURI().toString()), true).getContents();
					if(!contents.isEmpty()) {
						referencedModel = (org.vclipse.vcml.vcml.Model)contents.get(0);
					}

					newVcmlTResource = referencedModel.eResource().getResourceSet().createResource(URI.createURI(targetFile.getLocationURI().toString()));
					vcmltModel = VCMLT.createModel();
					TestCase testcase = VCMLT.createTestCase();
					
					if (referencedModel!=null) {
						// FIXME set header material
						// session.setItem(header material)
					}
					vcmltModel.setTestcase(testcase);									

					newVcmlTResource.getContents().add(vcmltModel);

					for(File file : page.getFilesToTransform()) {
						try {
							configScanXml2VcmlTImport.doImport(referencedModel, file, vcmltModel);
						} catch (SAXException e) {
							ErrorDialog.openError(getShell(), ERROR_DIALOG_TITLE, "Error in file " + file.getName(), new Status(IStatus.ERROR, 
									SYMBOLIC_NAME, e.getMessage()));
						} catch (IOException e) {
							ErrorDialog.openError(getShell(), ERROR_DIALOG_TITLE, "Error in file " + file.getName(), new Status(IStatus.ERROR, 
									SYMBOLIC_NAME, e.getMessage()));
						}
						monitor.worked(1);
					}

					try {
						newVcmlTResource.save(SaveOptions.newBuilder().format().noValidation().getOptions().toOptionsMap());
						page.getTargetContainer().refreshLocal(1, monitor);
					} catch (IOException e) {
						ErrorDialog.openError(getShell(), ERROR_DIALOG_TITLE, "Can not save the generated vcmlt file " + targetFile.getName(), new Status(IStatus.ERROR, 
								SYMBOLIC_NAME, e.getMessage()));
					} catch (CoreException e) {
						ErrorDialog.openError(getShell(), ERROR_DIALOG_TITLE, "Can not refresh the target container " + page.getTargetContainer().getName(), new Status(IStatus.ERROR, 
								SYMBOLIC_NAME, e.getMessage()));
					}
					monitor.done();
					if(page.openTargetFile()) {
						try {
							IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), targetFile);
						} catch (PartInitException e) {
							ErrorDialog.openError(getShell(), ERROR_DIALOG_TITLE, "Can not open the generated vcmlt file " + targetFile.getName(), new Status(IStatus.ERROR, 
									SYMBOLIC_NAME, e.getMessage()));
						}
					}
				}});
		} catch (InvocationTargetException e) {
			ErrorDialog.openError(getShell(), ERROR_DIALOG_TITLE, e.getMessage(), new Status(IStatus.ERROR, 
					SYMBOLIC_NAME, e.getMessage()));
			e.printStackTrace();
		} catch (InterruptedException e) {
			ErrorDialog.openError(getShell(), ERROR_DIALOG_TITLE, e.getMessage(), new Status(IStatus.ERROR, 
					SYMBOLIC_NAME, e.getMessage()));
		}
		return true;
	}
}
