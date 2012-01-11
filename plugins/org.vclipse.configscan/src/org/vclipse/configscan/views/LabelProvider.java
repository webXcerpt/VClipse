package org.vclipse.configscan.views;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.IImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.utils.DocumentUtility;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

public final class LabelProvider extends ColumnLabelProvider  {

	@Inject
	private DocumentUtility documentUtility;
	
	@Inject
	private IImageHelper imageHelper;
	
	
	private static final String EMPTY = "";
	
	protected Map<Element, Element> elementMap;
	
	protected Map<Element, URI> inputToUri;
	
	private ResourceSet resourceSet = new XtextResourceSet();
	
	private boolean shouldDelegate;
	
	private ILabelProvider labelProviderExtension;
	
	public LabelProvider() {
		elementMap = Maps.newHashMap();
		inputToUri = Maps.newHashMap();
	}
	
	public void setDelegate(boolean delegate) {
		this.shouldDelegate = delegate;
	}
	
	public void setLabelProviderExtension(ILabelProvider extension) {
		this.labelProviderExtension = extension;
	}
	
	@Override
	public String getText(Object object) {
		if(shouldDelegate && labelProviderExtension != null) {
			return labelProviderExtension.getText(object);
		} else {
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
		}
		return EMPTY;
	}
	
	@Override
	public Image getImage(Object object) {
		if(shouldDelegate && labelProviderExtension != null) {
			return labelProviderExtension.getImage(object);
		} else {
			if(object instanceof Element) {
				if(object instanceof Element) {
					return documentUtility.hasSuccessStatus((Element)object) ? 
							imageHelper.getImage(IConfigScanImages.SUCCESS) : imageHelper.getImage(IConfigScanImages.ERROR);
				}
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
			if(eObject != null && shouldDelegate && labelProviderExtension != null) {
				tooltip += "\n" + labelProviderExtension.getText(eObject);
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

	
}
