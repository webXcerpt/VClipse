package org.vclipse.configscan.imports;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.ecore.EObject;
import org.xml.sax.SAXException;

public abstract class AbstractConfigScanImportTransformation implements IConfigScanImportTransformation {

	protected EObject targetModel;
	
	protected EObject referencedModel;
	
	public void setTargetModel(EObject targetModel) {
		this.targetModel = targetModel;
	}
	
	public void setReferencedModel(EObject referencedModel) {
		this.referencedModel = referencedModel;
	}
	
	abstract public void doImport(File file2Import) throws SAXException, IOException;

	abstract public void init();

	abstract public String getReferencedModelExtension();

	abstract public String getTargetModelExtension();
}
