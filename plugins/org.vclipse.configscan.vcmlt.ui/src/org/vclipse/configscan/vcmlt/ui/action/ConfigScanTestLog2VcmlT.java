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
