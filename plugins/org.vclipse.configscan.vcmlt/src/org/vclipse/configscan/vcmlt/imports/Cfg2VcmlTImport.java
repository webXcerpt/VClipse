package org.vclipse.configscan.vcmlt.imports;

import java.io.File;
import java.io.IOException;

import org.vclipse.configscan.imports.DefaultModel2ModelTransformation;
import org.xml.sax.SAXException;

public class Cfg2VcmlTImport extends DefaultModel2ModelTransformation {
	
	@Override
	public void init() {
		// executed only once => referenced and target models are set 
		
//		if(referencedModel!=null) {
//		// FIXME set header material
//		// session.setItem(header material)
//	}
	}

	@Override
	public void doImport(File file2Import) throws SAXException, IOException {
		System.err.print("Importing file " + file2Import.getName());
	}

	@Override
	public String getReferencedModelExtension() {
		return "vcml";
	}

	@Override
	public String getTargetModelExtension() {
		return "vcmlt";
	}
}
