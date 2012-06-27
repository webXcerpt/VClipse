package org.vclipse.configscan.vcmlt.ui.action;

import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.configscan.vcmlt.vcmlT.Model;
import org.vclipse.configscan.vcmlt.vcmlT.VcmlTFactory;
import org.w3c.dom.Document;

public class ConfigScanTestLog2VcmlT {

	private static final VcmlTFactory VCMLT = VcmlTFactory.eINSTANCE;
	
	public Model convert(Document configScanLogDoc, Resource vcmlResource) {
		Model testModel = VCMLT.createModel();
		// TODO implement this
		return testModel;
	}

}
