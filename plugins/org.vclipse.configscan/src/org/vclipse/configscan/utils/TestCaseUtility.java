package org.vclipse.configscan.utils;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;

public class TestCaseUtility {

	private static final Map<Element, URI> emptyMapInputUri = Maps.newHashMap();
	
	private static final Map<Element, Element> emptyMapLogInput = Maps.newHashMap();
	
	public TestCase getRoot(TestCase testCase) {
		TestCase parent = testCase.getParent();
		if(parent == null) {
			return testCase;
		} else {
			return getRoot(parent);
		}
	}
	
	public TestCase createTestCase(Element element, TestCase parent, DocumentUtility documentUtility, Map<String, Object> options) {
		return createTestCase(element, parent, documentUtility, options, emptyMapInputUri, emptyMapLogInput);
	}
	
	public TestCase createTestCase(Element element, TestCase parent, DocumentUtility documentUtility, Map<String,Object> options, Map<Element, URI> inputToUriMap, Map<Element, Element> mapLogInput) {
		if(documentUtility.passesNodeFilter(element) && documentUtility.passesOptionsFilter(element, options)) {
			TestCase testCase = new TestCase();
			testCase.setTitle(element.getAttribute(DocumentUtility.TITLE));
			testCase.setStatus(element.getAttribute(DocumentUtility.ATTRIBUTE_STATUS).equals(DocumentUtility.STATUS_SUCCESS) 
					? Status.SUCCESS : Status.FAILURE );

			Element inputElement = mapLogInput.get(element);
			testCase.setInputElement(inputElement);
			testCase.setLogElement(element);
			testCase.setSourceUri(inputToUriMap.get(inputElement));
			testCase.setParent(parent);

			NodeList childNodes = element.getChildNodes();
			for(int i=0; i<childNodes.getLength(); i++) {
				Node item = childNodes.item(i);
				if(Node.ELEMENT_NODE == item.getNodeType()) {
					TestCase childTestCase = createTestCase((Element)item, testCase, documentUtility, options, inputToUriMap, mapLogInput);
					if(childTestCase != null) {
						testCase.addTestCase(childTestCase);						
					}
				}
			}
			return testCase;
		}
		return null;
	}
}
