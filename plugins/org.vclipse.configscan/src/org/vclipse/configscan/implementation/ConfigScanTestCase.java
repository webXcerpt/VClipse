package org.vclipse.configscan.implementation;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IElementCollector;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.IConfigScanTestObject;
import org.vclipse.configscan.utils.DocumentUtility;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;

public final class ConfigScanTestCase implements IConfigScanTestObject {

	private IConfigScanTestObject parent;
	
	private Element logElement;
	
	private Element inputElement;

	private URI uri;
	
	private ConfigScanImageHelper imageHelper;
	
	private DocumentUtility documentUtility;
	
	private Map<Element, URI> inputToUriMap;
	
	private Map<Element, Element> mapLogInput;
	
	private List<IConfigScanTestObject> testCases;
	
	public ConfigScanTestCase(IConfigScanTestObject parent, Map<Element, URI> inputToUriMap, Map<Element, Element> mapLogInput) {
		this.parent = parent;
		this.inputToUriMap = inputToUriMap;
		this.mapLogInput = mapLogInput;
		testCases = Lists.newArrayList();
	}
	
	public void setDocumentUtility(DocumentUtility documentUtility) {
		this.documentUtility = documentUtility;
	}
	
	public void setImageHelper(ConfigScanImageHelper imageHelper) {
		this.imageHelper = imageHelper;
	}
	
	public void setLogElement(Element logElement) {
		this.logElement = logElement;
	}
	
	public void setInputElement(Element inputElement) {
		this.inputElement = inputElement;
	}
	
	public void setTestStatementUri(URI uri) {
		this.uri = uri;
	}
	
	public ConfigScanTestRun getTestRun() {
		Object topParent = parent;
		while(topParent != null && !(topParent instanceof ConfigScanTestRun)) {
			topParent = ((ConfigScanTestCase)topParent).getParent(null);
		}
		return (ConfigScanTestRun)topParent;
	}
	
	public Element getLogElement() {
		return logElement;
	}
	
	public Element getInputElement() {
		return inputElement;
	}
	
	public URI getTestStatementUri() {
		return uri;
	}
	
	public Object[] getChildren(Object object) {
		List<Node> children = Lists.newArrayList();
		NodeList childNodes = logElement.getChildNodes();
		for(int i=0; i<childNodes.getLength(); i++) {
			children.add(childNodes.item(i));
		}
		return children.toArray();
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return documentUtility.hasSuccessStatus(logElement) 
				? imageHelper.getImageDescriptor(IConfigScanImages.SUCCESS) 
						: imageHelper.getImageDescriptor(IConfigScanImages.ERROR);
	}

	public String getLabel(Object o) {
		return logElement.getAttribute(DocumentUtility.TITLE);
	}

	public Object getParent(Object object) {
		return parent;
	}

	public void fetchDeferredChildren(Object object, IElementCollector collector, IProgressMonitor monitor) {
		testCases = Lists.newArrayList();
		NodeList childNodes = logElement.getChildNodes();
		for(int i=0; i<childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if((item.getNodeType() == Node.ELEMENT_NODE)) { 
				Element childElement = (Element)item;
				if(documentUtility.passesFilter(childElement)) {
					ConfigScanTestCase testCase = new ConfigScanTestCase(this, inputToUriMap, mapLogInput);
					testCase.setLogElement(childElement);
					Element inputElement = mapLogInput.get(childElement);
					testCase.setInputElement(inputElement);
					testCase.setTestStatementUri(inputToUriMap.get(inputElement));
					testCase.setImageHelper(imageHelper);
					testCase.setDocumentUtility(documentUtility);
					testCases.add(testCase);
				}
			}
		}
		for(IConfigScanTestObject testCase : testCases) {
			collector.add(testCase, monitor);
		}
	}

	public boolean isContainer() {
		return logElement.hasChildNodes();
	}

	public ISchedulingRule getRule(Object object) {
		return null;
	}
}
