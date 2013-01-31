/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator and others
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan.vcmlt.imports;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

public class ConfigScanXml2VcmlTImportTransformation extends Abstract2VcmlTImportTransformation {
	
	public void doImport(File file2Import) throws SAXException, IOException {
		// FIXME implemnet this
		System.err.print("Importing file " + file2Import.getName());
	}
}