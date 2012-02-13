package org.vclipse.configscan.utils;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.ITestObjectFilter;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestRun;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class TestCaseFactory {

	@Inject
	private DocumentUtility documentUtility;
	
	@Inject
	private ITestObjectFilter objectFilter;
	
	@Inject
	private Provider<TestRun> testRunProvider;
	
	private Map<Element, URI> input2Uri;
	
	private Map<Element, Element> log2Input;
	
	private Map<Object, Object> options;
	
	public TestCaseFactory() {
		options = Maps.newHashMap();
		input2Uri = Maps.newHashMap();
		log2Input = Maps.newHashMap();
	}

	public void setInputUriMap(Map<Element, URI> input2Uri) {
		this.input2Uri = input2Uri;
	}
	
	public void setLogInputMap(Map<Element, Element> log2Input) {
		this.log2Input = log2Input;
	}
	
	public void setOptions(Map<Object, Object> options) {
		this.options = options;
	}
	
	public TestRun buildTestRun(String fileName, RemoteConnection connection, IConfigScanXMLProvider xmlProvider, EObject testModel) {
		TestRun testRun = testRunProvider.get();
		testRun.setTitle(connection == null ? fileName : fileName + " on " + connection.getDescription());
		testRun.setRemoteConnection(connection);
		testRun.setXmlProvider(xmlProvider);
		testRun.setTestModel(testModel);
		testRun.setFilter(objectFilter);
		testRun.setOptions(options);
		return testRun;
	}
	
	public TestCase buildTestCase(TestRun testRun) {
		return new TestCase(testRun);
	}
	
	public TestCase buildTestCase(Document document, TestCase parent) {
		Node logSession = documentUtility.getLogSession(document);
		if(logSession instanceof Element) {
			return buildTestCase((Element)logSession, parent);
		}
		
		// could not extract session element from the document
		// a test case can not be created
		return null;
	}
	
	public TestCase buildTestCase(Element element, TestCase parent) {
		if(documentUtility.passesNodeFilter(element)) {
			NodeList nodeList = element.getChildNodes();
			// there are no child nodes, create a test case
			if(nodeList.getLength() == 0 && element.getNodeName().equals(DocumentUtility.NODE_NAME_LOG_MSG)) {
				TestCase testCase = new TestCase(parent);
				testCase.setLogElement(element);
				Element inputNode = log2Input.get(element);
				testCase.setInputElement(inputNode);
				testCase.setSourceURI(input2Uri.get(inputNode));
				return testCase;
			} else {
				// create a test group
				TestGroup testGroup = new TestGroup(parent);
				testGroup.setLogElement(element);
				Element inputNode = log2Input.get(element);
				testGroup.setInputElement(inputNode);
				testGroup.setSourceURI(input2Uri.get(inputNode));
				
				for(int i=0; i<nodeList.getLength(); i++) {
					Node item = nodeList.item(i);
					if(Node.ELEMENT_NODE == item.getNodeType()) {
						TestCase childTestCase = buildTestCase((Element)item, testGroup);
						if(childTestCase == null) {
							break;
						} else {
							testGroup.addTestCase(childTestCase);
						}
					}
				}
				return testGroup;
			}
		}
		return null;
	}
}
