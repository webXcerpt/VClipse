package org.vclipse.configscan.views;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.utils.DocumentUtility;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class DefaultConfigScanLabelProvider extends ColumnLabelProvider implements IConfigScanLabelProvider {

	private static final String EMPTY = "";
	
	private ImageRegistry imageRegistry;
	
	@Inject
	private DocumentUtility documentUtility;
	
	protected Map<Element, Element> elementMap;
	
	protected Map<Element, URI> inputToUri;
	
	private ResourceSet resourceSet;
	
	public DefaultConfigScanLabelProvider() {
	
		imageRegistry = ConfigScanPlugin.getDefault().getImageRegistry();
		elementMap = Maps.newHashMap();
		inputToUri = Maps.newHashMap();
	}
	
	@Override
	public String getText(Object object) {
		if(object instanceof Element) {
			Element element = (Element)object;
			if(DocumentUtility.LOG_SESSION.equals(element.getTagName())) {
				return element.getAttribute(DocumentUtility.TITLE);
			} else if(DocumentUtility.LOG_TEST_GRP.equals(element.getTagName())) {
				return element.getAttribute(DocumentUtility.TITLE);
			} else if(DocumentUtility.NODE_NAME_LOG_MSG.equals(element.getTagName())) {
				return element.getAttribute(DocumentUtility.TITLE);
			} else {
				return element.getAttribute(DocumentUtility.TITLE);
			}
		}
		return EMPTY;
	}
	
	@Override
	public Image getImage(Object object) {
		if(object instanceof Element) {
			if(object instanceof Element) {
				return documentUtility.isSuccess((Element)object) ? 
						imageRegistry.get(IConfigScanImages.SUCCESS) : imageRegistry.get(IConfigScanImages.WARNING);							
			}
		}
		return null;
	}

	@Override
	public int getToolTipDisplayDelayTime(Object object) {
		return 500;
	}
	
	@Override
	public int getToolTipTimeDisplayed(Object object) {
 		return 10000;
 	}
	
	public Map<Element, Element> getMapLogInput() {
		return this.elementMap;
	}
	
	public Map<Element, URI> getMapInputUri() {
		return this.inputToUri;
	}

 	protected EObject getEObjectForUri(URI uri) {
 		return resourceSet.getEObject(uri, true);
 	}
 	
 	public String getToolTipText(Object object) {
 		if(elementMap != null && object instanceof Element) {
 			Element element = (Element) object;
 			if(!element.hasChildNodes()) {
 				return computeTooltipText(element);
 			}
 		}
 		return EMPTY;
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
				//tooltip += "\nCMLT: " + delegate.getText(eObject);
			}
			return tooltip;
		}
		return "";
	}
 	
 	public void setElementMap(Map<Element, Element> element2ElementMap) {
		this.elementMap = element2ElementMap;
	}
	
	public void setInputEObjectMap(Map<Element, URI> inputToEObject) {
		this.inputToUri = inputToEObject;
	}

	@Override
	public Element getIputElement(Element logElement) {
		return elementMap.get(logElement);
	}

	@Override
	public Element getLogElement(Element inputElement) {
		for(Entry<Element, Element> entry : elementMap.entrySet()) {
			if(entry.getValue().equals(inputElement)) {
				return entry.getKey();
			}
		}
		return null;
	}

	@Override
	public URI getInputUri(Element inputElement) {
		return inputToUri.get(inputElement);
	}

	@Override
	public Element getInputElement(URI uri) {
		for(Entry<Element,URI> entry : inputToUri.entrySet()) {
			if(entry.getValue().equals(uri)) {
				return entry.getKey();
			}
		}
		return null;
	}
}
