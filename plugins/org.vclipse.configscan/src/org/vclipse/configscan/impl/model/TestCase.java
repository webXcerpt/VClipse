package org.vclipse.configscan.impl.model;

import org.eclipse.emf.common.util.URI;
import org.vclipse.configscan.utils.DocumentUtility;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TestCase {
	
	public enum Status {
		SUCCESS, FAILURE
	}
	
	public TestCase(TestCase parent) {
		this.parent = parent;
	}
	
	private TestCase parent;
	
	private URI sourceURI;
	
	private Node inputNode;
	
	private Node logNode;
	
	public String getTitle() {
		if(logNode instanceof Element) {
			return ((Element)logNode).getAttribute(DocumentUtility.ATRRIBUTE_TITLE);			
		} 
		return null;
	}
	
	public Status getStatus() {
		if(logNode instanceof Element) {
			String statusAttribute = ((Element)logNode).getAttribute(DocumentUtility.ATTRIBUTE_STATUS);
			return statusAttribute.equals(DocumentUtility.STATUS_SUCCESS) ? Status.SUCCESS : Status.FAILURE;
		}
		return Status.FAILURE;
	}
	
	public void setParent(TestCase parent) {
		this.parent = parent;
	}
	
	public void setSourceURI(URI sourceURI) {
		this.sourceURI = sourceURI;
	}
	
	public void setInputElement(Node inputNode) {
		this.inputNode = inputNode;
	}
	
	public void setLogElement(Node logNode) {
		this.logNode = logNode;
	}

	public TestCase getParent() {
		return parent;
	}

	public URI getSourceURI() {
		return sourceURI;
	}

	public Node getInputElement() {
		return inputNode;
	}

	public Node getLogElement() {
		return logNode;
	}

	public TestCase getRoot() {
		return getRoot(this);
	}
	
	protected TestCase getRoot(TestCase testCase) {
		TestCase parent = testCase.getParent();
		if(parent == null) {
			return testCase;
		} else {
			return getRoot(parent);
		}
	}
	
	@Override
	public String toString() {
		return "Title={" + getTitle() + "} Status={" + getStatus() + "}";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TestCase) {
			TestCase toCompare = (TestCase)obj;
			return getTitle().equals(toCompare.getTitle()) && getStatus().equals(toCompare.getStatus());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
		result = prime * result + getStatus().hashCode();
		return result;
	}
}
