package org.vclipse.configscan.vcmlt.ui.action;

import org.eclipse.emf.ecore.EObject;
import org.vclipse.configscan.actions.AbstractConfigScanXmlHandler;
import org.vclipse.configscan.vcmlt.vcmlT.Model;
import org.vclipse.configscan.vcmlt.vcmlT.TestCase;

public class ConvertVcmlT2ConfigScanXmlHandler extends AbstractConfigScanXmlHandler {

	@Override
	public String getFileExtension() {
		return "VcmlT";
	}

	@Override
	public void testProperties(EObject model) {
		if(model instanceof Model) {
			Model vcmltModel = (Model)model;
			TestCase testcase = vcmltModel.getTestcase();
			if(testcase == null) {
				throw new IllegalArgumentException("no testcase");
			}
		} else {
			throw new IllegalArgumentException("no model");
		}
	}
}
