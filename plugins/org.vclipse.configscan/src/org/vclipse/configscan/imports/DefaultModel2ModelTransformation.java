package org.vclipse.configscan.imports;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.ecore.EObject;
import org.xml.sax.SAXException;

public class DefaultModel2ModelTransformation implements IConfigScanImportTransformation {

	protected EObject targetModel;
	
	protected EObject referencedModel;
	
	public void setTargetModel(EObject targetModel) {
		this.targetModel = targetModel;
	}
	
	public void setReferencedModel(EObject referencedModel) {
		this.referencedModel = referencedModel;
	}
	
	@Override
	public void doImport(File file2Import) throws SAXException, IOException {
	}

	@Override
	public void init() {
	}

	@Override
	public String getReferencedModelExtension() {
		return null;
	}

	@Override
	public String getTargetModelExtension() {
		return null;
	}
}
