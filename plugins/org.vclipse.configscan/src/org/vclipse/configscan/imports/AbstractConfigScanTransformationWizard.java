package org.vclipse.configscan.imports;

import java.io.File;
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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.StringInputStream;
import org.vclipse.configscan.ConfigScanPlugin;

import com.google.inject.Inject;

public abstract class AbstractConfigScanTransformationWizard extends Wizard implements IImportWizard {
	
	private IConfigScanImportTransformation transformation;
	
	private WizardPage wizardPage;
	
	private XtextResourceSet resourceSet;
	
	@Inject
	public AbstractConfigScanTransformationWizard(IConfigScanImportTransformation transformation) {
		this.transformation = transformation;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		wizardPage = new WizardPage("", selection, transformation);
		setNeedsProgressMonitor(true);
		resourceSet = new XtextResourceSet();
	}

	public void addPages() {
		addPage(wizardPage);
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new WorkspaceModifyOperation() {
				@Override
				protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
					try {
						monitor.beginTask(getTaskString(), IProgressMonitor.UNKNOWN);
						
						IFile exportLocation = wizardPage.getExportLocation();
						if(exportLocation.exists() && wizardPage.overwriteExistigTargetFile()) {
							exportLocation.setContents(new StringInputStream(""), IResource.KEEP_HISTORY, monitor);
						} else {
							exportLocation.create(new StringInputStream(""), true, monitor);
						}
						
						Resource export2Resource = resourceSet.createResource(URI.createURI(exportLocation.getLocationURI().toString()));
						EObject targetModel = createTargetModel();
						export2Resource.getContents().add(targetModel);
						
						EObject referencedModel = null;
						EList<EObject> contents = resourceSet.getResource(
								URI.createURI(wizardPage.getSourceLocation().getLocationURI().toString()), true).getContents();
						if(!contents.isEmpty()) {
							referencedModel = contents.get(0);
						}
					
						transformation.setReferencedModel(referencedModel);
						transformation.setTargetModel(targetModel);
						transformation.init();
						
						for(File currentFile : wizardPage.getFilesToTransform()) {
							transformation.doImport(currentFile);
							monitor.worked(1);
						}

						export2Resource.save(SaveOptions.newBuilder().format().noValidation().getOptions().toOptionsMap());
						wizardPage.getTargetContainer().refreshLocal(1, monitor);
						monitor.done();
						
						if(wizardPage.openTargetFile()) {
							IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), exportLocation);
						}
					} catch(Exception exception) {
						throw new InvocationTargetException(exception);
					}
				}
			});
		} catch (InvocationTargetException exception) {
			ErrorDialog.openError(getShell(), "Error during ConfigScan import transformation", "Can not execute ConfigScan transformation", 
					new Status(IStatus.ERROR, ConfigScanPlugin.ID, exception.getMessage()));
			return false;
		} catch (InterruptedException exception) {
			ErrorDialog.openError(getShell(), "Error during ConfigScan import transformation", "Can not execute ConfigScan transformation", 
					new Status(IStatus.ERROR, ConfigScanPlugin.ID, exception.getMessage()));
			return false;
		}
		return true;
	}
	
	protected abstract EObject createTargetModel();
	
	protected abstract String getTaskString();
}
