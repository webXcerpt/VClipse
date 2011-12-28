package org.vclipse.configscan.views;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.w3c.dom.Element;

public class ViewOtherLabelProvider extends ViewLabelProvider {
//	private Map<Element, Element> elementMap;
//	private Map<Element, EObject> inputToEObject;

	
	public ViewOtherLabelProvider(Map<Element, Element> mapLogInput,
			Map<Element, URI> inputToUri) {
		super(mapLogInput, inputToUri);
		
	}

	public String getText(Object obj) {
		if (obj instanceof Element) {
			
			Element elementLog = (Element) obj;
			
			if("1".equals(elementLog.getAttribute("level"))) {
				return elementLog.getAttribute("title");
			}
			
			Element elementInput = elementMap.get(elementLog);
			if(elementInput != null) {
				EObject eObject = getEObjectForUri(inputToUri.get(elementInput));
				
				if(eObject != null) {
					return computeLabel(elementLog, elementInput, testLanguageLabelProvider.getText(eObject));
				}
			}
		}
		return "";
	}

	private String computeLabel(Element elementLog, Element elementInput, String text) {
		if("log_msg".equals(elementLog.getNodeName()) /*&& "E".equals(elementLog.getAttribute("status"))*/) {
			String cmd = elementLog.getAttribute("cmd");
			boolean e = "E".equals(elementLog.getAttribute("status"));
			if("CHECK ITEM QUANTITY".equals(cmd)) {
				if(elementLog.getAttribute("title").contains("Instance not found")) {
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
					return text + " (computed: " + ("false".equals(test) ? "0" : "1" ) + ")";
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
