package org.vclipse.configscan.vcmlt.ui.imports;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.vclipse.configscan.imports.IConfigScanImportTransformation;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ConfigScanXml2VcmlTImportWizard extends Abstract2VcmlTImportWizard {

	@Inject
	public ConfigScanXml2VcmlTImportWizard(@Named("ConfigScanXml2VcmlTImportTransformation") IConfigScanImportTransformation transformation) {
		super(transformation);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setWindowTitle("Import wizard for VCMLT test cases from ConfigScan (*.xml) files");
	}

	@Override
	protected String getTaskString() {
		return "Transforming ConfigScan XML input files to VCMLT files...";
	}
}
