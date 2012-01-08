/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.configscan;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.configscan.implementation.ConfigScanXmlProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.inject.ImplementedBy;

@ImplementedBy(ConfigScanXmlProvider.class)
public interface IConfigScanXMLProvider {
	
	public Document transform(EObject model,  Map<Element, URI> map);
	
	public HashMap<Element, Element> computeConfigScanMap(Document xmlLog, Document xmlInput);
	
	public String getMaterialNumber(EObject model);
	
	public String getBomApplication(EObject model);
}
