package org.vclipse.configscan.vcmlt.ui.imports;

import org.eclipse.emf.ecore.EObject;
import org.vclipse.configscan.imports.AbstractConfigScanTransformationWizard;
import org.vclipse.configscan.imports.IConfigScanImportTransformation;
import org.vclipse.configscan.vcmlt.vcmlT.Model;
import org.vclipse.configscan.vcmlt.vcmlT.VcmlTFactory;

public abstract class Abstract2VcmlTImportWizard extends AbstractConfigScanTransformationWizard {

	public Abstract2VcmlTImportWizard(IConfigScanImportTransformation transformation) {
		super(transformation);
	}
	
	protected EObject createTargetModel() {
		VcmlTFactory vcmltFactory = VcmlTFactory.eINSTANCE;
		Model vcmltModel = vcmltFactory.createModel();
		vcmltModel.setTestcase(vcmltFactory.createTestCase());
		return vcmltModel;
	}

}
