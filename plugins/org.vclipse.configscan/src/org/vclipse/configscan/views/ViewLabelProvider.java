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
package org.vclipse.configscan.views;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.vclipse.configscan.ConfigScanPlugin;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.google.inject.Injector;

class ViewLabelProvider extends ColumnLabelProvider {

	protected AbstractUIPlugin plugin;
	protected ImageRegistry imageRegistry;
	protected Map<Element, Element> elementMap;
	protected Map<Element, URI> inputToUri;
	protected ILabelProvider testLanguageLabelProvider;
	protected ResourceSet resSet;
	
	/**
	 * @param sampleView
	 */
	ViewLabelProvider() {
		plugin = ConfigScanPlugin.getDefault();
		imageRegistry = plugin.getImageRegistry();
		this.elementMap = null;
		this.inputToUri = null;
		this.resSet = null;
		testLanguageLabelProvider = null;
		
	}

	public ViewLabelProvider(Map<Element, Element> elementMap, Map<Element, URI> inputToEObject) {	// TODO: objectMap
		plugin = ConfigScanPlugin.getDefault();
		imageRegistry = plugin.getImageRegistry();
		this.elementMap = elementMap;
		this.inputToUri = inputToEObject;
		this.resSet = new XtextResourceSet();
		Injector injector = ConfigScanPlugin.getDefault().getInjector();
		testLanguageLabelProvider = injector.getInstance(ILabelProvider.class);
	}
	
	

	public String getText(Object obj) {
		if (obj instanceof Element) {
			Element element = (Element)obj;
        	String tagName = element.getTagName();
        	if ("log_session".equals(tagName)) {
        		return element.getAttribute("title");
        	} else if ("log_testgrp".equals(tagName)) {
        		return element.getAttribute("title");
        	} else if ("log_msg".equals(tagName)) {
        		return element.getAttribute("title");
        	} else {
        		return element.getAttribute("title");
        	}
		}
		return null;
	}
			
	public Image getImage(Object obj) {
		if (obj instanceof Element) {
			Element element = (Element)obj;
			return Util.isSuccess(element) ? imageRegistry.get("s") : imageRegistry.get("f");			
		}
		return null;
	}
	
	
 	public String getToolTipText(Object element) {
 		if(elementMap != null && element instanceof Element) {
 			Element el = (Element) element;
 			if(!el.hasChildNodes()) {
 				return computeTooltipText(el);
 			}
 		}
 		return null;
 	}
 	
 	
 	
 	public int getToolTipDisplayDelayTime(Object object) {
 		return 500;
 	}
 	
 	
 	public int getToolTipTimeDisplayed(Object object) {
 		return 10000;
 	}
 	
 	
	public String computeTooltipText(Element elementLog) {
		Element elementInput = elementMap.get(elementLog);
		if(elementInput != null) {
			String tooltip = "ConfigScan XML LOG: " + elementInput.getTagName();
			
			NamedNodeMap nnm = elementInput.getAttributes();
			for(int i = 0; i < nnm.getLength(); i++) {
				Node node = nnm.item(i);
				tooltip += " " + node.getNodeName() + "=\"" + node.getNodeValue() + "\"";
			}
			URI uri = inputToUri.get(elementInput);
			EObject eObject = getEObjectForUri(uri);
			if(eObject != null) {
				tooltip += "\nCMLT: " + testLanguageLabelProvider.getText(eObject);
			}
			return tooltip;
		}
		return "";
	}
	

	public Map<Element, Element> getMapLogInput() {
		return this.elementMap;
	}
	
	public Map<Element, URI> getMapInputUri() {
		return this.inputToUri;
	}


 	protected EObject getEObjectForUri(URI uri) {
 		EObject eObj = resSet.getEObject(uri, true);
 		return eObj;
 	}
 	
}