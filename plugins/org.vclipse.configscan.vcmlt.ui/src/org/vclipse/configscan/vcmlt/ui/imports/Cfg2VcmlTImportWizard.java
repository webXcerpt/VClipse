package org.vclipse.configscan.vcmlt.ui.imports;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.vclipse.configscan.imports.IConfigScanImportTransformation;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Cfg2VcmlTImportWizard extends Abstract2VcmlTImportWizard {

	@Inject
	public Cfg2VcmlTImportWizard(@Named("Cfg2VcmlTImportTransformation") IConfigScanImportTransformation transformation) {
		super(transformation);
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setWindowTitle("Import wizard for VCMLT test cases from IPC export (*.cfg or *.xml) files");
	}

	protected String getTaskString() {
		return "Transforming cfg/xml files to VCMLT files...";
	}
	
	
}
