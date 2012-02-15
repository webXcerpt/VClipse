package org.vclipse.configscan.vcmlt.imports;

import org.vclipse.configscan.imports.AbstractConfigScanImportTransformation;

public abstract class Abstract2VcmlTImportTransformation extends AbstractConfigScanImportTransformation {
	
	public void init() {
		// executed only once => referenced and target models are set 
		
//		if(referencedModel!=null) {
//		// FIXME set header material
//		// session.setItem(header material)
//	}
	}

	public String getReferencedModelExtension() {
		return "vcml";
	}

	public String getTargetModelExtension() {
		return "vcmlt";
	}
}
