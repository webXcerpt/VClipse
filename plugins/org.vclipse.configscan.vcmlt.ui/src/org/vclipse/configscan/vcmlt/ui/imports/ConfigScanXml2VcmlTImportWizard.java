package org.vclipse.configscan.vcmlt.ui.imports;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.vclipse.configscan.imports.DefaulConfigScanTransformationWizard;
import org.vclipse.configscan.imports.IConfigScanImportTransformation;
import org.vclipse.configscan.vcmlt.vcmlT.Model;
import org.vclipse.configscan.vcmlt.vcmlT.VcmlTFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ConfigScanXml2VcmlTImportWizard extends DefaulConfigScanTransformationWizard {

	@Inject
	public ConfigScanXml2VcmlTImportWizard(@Named("ConfigScanXml2VcmlTImport") IConfigScanImportTransformation transformation) {
		super(transformation);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setWindowTitle("Import wizard for VCMLT test cases from ConfigScan (*.xml) files");
	}

	@Override
	protected EObject createTargetModel() {
		VcmlTFactory vcmltFactory = VcmlTFactory.eINSTANCE;
		Model vcmltModel = vcmltFactory.createModel();
		vcmltModel.setTestcase(vcmltFactory.createTestCase());
		return vcmltModel;
	}

	@Override
	protected String getTaskString() {
		return "Transforming ConfigScan XML input files to VCMLT files...";
	}
}
