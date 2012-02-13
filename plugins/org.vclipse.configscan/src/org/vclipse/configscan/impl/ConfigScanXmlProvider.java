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
package org.vclipse.configscan.impl;

import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.ITestObjectFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Default implementation for the {@link IConfigScanXMLProvider} interface.
 * 
 */
public class ConfigScanXmlProvider implements IConfigScanXMLProvider {

	public static final String EMPTY = "";
	
	protected DocumentBuilder documentBuilder;
	
	@Override
	public Document transform(EObject model, ITestObjectFilter filter, Map<Element, URI> map, Map<Object, Object> options) {
		try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch(ParserConfigurationException exception) {
			
		}
		return documentBuilder.newDocument();
	}

	@Override
	public String getMaterialNumber(EObject model) {
		return EMPTY;
	}

}
