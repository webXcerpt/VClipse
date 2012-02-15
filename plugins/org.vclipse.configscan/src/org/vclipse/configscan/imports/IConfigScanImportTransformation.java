package org.vclipse.configscan.imports;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.ecore.EObject;
import org.xml.sax.SAXException;

public interface IConfigScanImportTransformation {

	public void setReferencedModel(EObject model);
	
	public void setTargetModel(EObject model);
	
	/**
	 * executed only once
	 */
	public void init();
	
	public void doImport(File file2Import) throws SAXException, IOException;
	
	/**
	 * @return the extension of the referenced file, for example: vcml
	 */
	public String getReferencedModelExtension();
	
	/**
	 * @return the extension of the referenced file, for example: vcmlt
	 */
	public String getTargetModelExtension();
}
