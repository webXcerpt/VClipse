package org.vclipse.configscan.vcmlt.imports;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

public class ConfigScanXml2VcmlTImport extends Cfg2VcmlTImport {
	
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
}