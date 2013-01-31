/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
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
