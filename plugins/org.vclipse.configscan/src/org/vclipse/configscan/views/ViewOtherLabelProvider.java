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

import org.eclipse.jface.viewers.ILabelProvider;
import org.vclipse.configscan.utils.DocumentUtility;
import org.w3c.dom.Element;

public class ViewOtherLabelProvider extends DefaultConfigScanLabelProvider {

	public ViewOtherLabelProvider(ILabelProvider delegate) {
		super(delegate);
	}

	public String getText(Object obj) {
//		if(obj instanceof Element) {
//			Element elementLog = (Element) obj;
//			if(DocumentUtility.ATTRIBUTE_VALUE_ONE.equals(elementLog.getAttribute(DocumentUtility.ATTRIBUTE_LEVEL))) {
//				return elementLog.getAttribute(DocumentUtility.ATTRIBUTE_TITLE);
//			}
//			Element elementInput = elementMap.get(elementLog);
//			if(elementInput != null) {
//				EObject eObject = getEObjectForUri(inputToUri.get(elementInput));
//				
//				if(eObject != null) {
//					return computeLabel(elementLog, elementInput, testLanguageLabelProvider.getText(eObject));
//				}
//			}
//		}
		return "";
	}

	private String computeLabel(Element elementLog, Element elementInput, String text) {
		if(DocumentUtility.NODE_NAME_LOG_MSG.equals(elementLog.getNodeName()) /*&& "E".equals(elementLog.getAttribute("status"))*/) {
			String cmd = elementLog.getAttribute(DocumentUtility.ATTRIBUTE_CMD);
			boolean e = DocumentUtility.ATTRIBUTE_VALUE_E.equals(elementLog.getAttribute(DocumentUtility.ATTRIBUTE_STATUS));
			if("CHECK ITEM QUANTITY".equals(cmd)) {
				if(elementLog.getAttribute(DocumentUtility.TITLE).contains("Instance not found")) {
					return text + " (instance not found)";
				}
				String countStr = elementLog.getAttribute("quantity");
				if(e) {
					return text + " (computed: " + ("".equals(countStr) ? "> 0)" : countStr + ")");
				}
				else {
					return text + " (" + countStr + ")";
				}
			}
			else if("CHECK ITEM EXIST".equals(cmd)) {
				String test = elementLog.getAttribute("test");
				if(e) {
					return text + " (computed: " + ("false".equals(test) ? "0" : DocumentUtility.ATTRIBUTE_VALUE_ONE ) + ")";
				}
				else {
					return text + " (" + ("false".equals(test) ? "FALSE" : "TRUE" ) + ")";
				}
			}
			else if("CHECK ITEM STATUS".equals(cmd)) {
				return text + " (" + elementLog.getAttribute("v3") + ")";
			}
			else if("SET CSTIC VALUE".equals(cmd)) {
				return text + " (" + elementLog.getAttribute("v2") + ")";
			}
		}
		
		return text;
	}
}
