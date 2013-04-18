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
package org.vclipse.configscan.impl;

import java.util.HashMap;
import java.util.Map.Entry;

import org.vclipse.configscan.IConfigScanReverseXmlTransformation;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

import com.google.common.base.Strings;

/**
 * Default implementation for the {@link IConfigScanXMLProvider} interface.
 * 
 */
public class ConfigScanReverseXmlTransformation implements IConfigScanReverseXmlTransformation {

	/** This method creates the mapping between xmlLog and xmlInput. The key is xmlLog.
	 * 
	 */
	public HashMap<Element, Element> computeConfigScanMap(Document xmlLog, Document xmlInput) {
		HashMap<Element, Element> configScanMap = new HashMap<Element, Element>();
		HashMap<String, Element> logMap = computeXidMap(xmlLog);
		HashMap<String, Element> inputMap = computeXidMap(xmlInput);
		for (Entry<String, Element> logEntry : logMap.entrySet()) {
			String inputValue = logEntry.getKey();
			if (!Strings.isNullOrEmpty(inputValue)) {
				configScanMap.put(logEntry.getValue(), inputMap.get(inputValue));
			}
		}
		return configScanMap;
	}
	
	private HashMap<String, Element> computeXidMap(Document xml) {
		HashMap<String, Element> result = new HashMap<String, Element>();
		DocumentTraversal traversable = (DocumentTraversal) xml;
	    TreeWalker walker = traversable.createTreeWalker(xml.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, true);
		for (Node node = walker.firstChild(); node != null; node = walker.nextNode()) {
			if (node instanceof Element) {
				Element el = (Element)node;
				String xid = el.getAttribute("xid");
				if (!Strings.isNullOrEmpty(xid)) {
					result.put(xid, el);
				}
			}
		}
		return result;
	}
	
}
