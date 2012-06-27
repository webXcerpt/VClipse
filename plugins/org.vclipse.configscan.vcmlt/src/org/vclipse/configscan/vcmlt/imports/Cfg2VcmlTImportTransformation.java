package org.vclipse.configscan.vcmlt.imports;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

public class Cfg2VcmlTImportTransformation extends Abstract2VcmlTImportTransformation {
	
	public void doImport(File file2Import) throws SAXException, IOException {
		// FIXME implemnet this
		System.err.print("Importing file " + file2Import.getName());
	}
}
