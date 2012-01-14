package org.vclipse.configscan.utils;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class TestCaseUtility {

	private static final Map<Element, URI> emptyMapInputUri = Maps.newHashMap();
	
	private static final Map<Element, Element> emptyMapLogInput = Maps.newHashMap();
	
	@Inject
	private DocumentUtility documentUtility;
	
	public TestCase getRoot(TestCase testCase) {
		TestCase parent = testCase.getParent();
		if(parent == null) {
			return testCase;
		} else {
			return getRoot(parent);
		}
	}
	
	public TestCase createTestCase(Element element, TestCase parent) {
		return createTestCase(element, parent, emptyMapInputUri, emptyMapLogInput);
	}
	
	public TestCase createTestCase(Element element, TestCase parent, Map<Element, URI> inputToUriMap, Map<Element, Element> mapLogInput) {
		if(documentUtility.passesFilter(element)) {
			TestCase testCase = new TestCase();
			testCase.setTitle(element.getAttribute(DocumentUtility.TITLE));
			testCase.setStatus(element.getAttribute(DocumentUtility.ATTRIBUTE_STATUS).equals(DocumentUtility.STATUS_SUCCESS) 
					? Status.SUCCESS : Status.FAILURE );
			
			Element inputElement = mapLogInput.get(element);
			testCase.setSourceUri(inputToUriMap.get(inputElement));
			testCase.setParent(parent);
			
			NodeList childNodes = element.getChildNodes();
			for(int i=0; i<childNodes.getLength(); i++) {
				Node item = childNodes.item(i);
				if(Node.ELEMENT_NODE == item.getNodeType()) {
					TestCase childTestCase = createTestCase((Element)item, testCase, inputToUriMap, mapLogInput);
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
