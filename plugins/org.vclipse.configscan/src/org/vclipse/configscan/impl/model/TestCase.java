package org.vclipse.configscan.impl.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.w3c.dom.Element;

import com.google.common.collect.Lists;

public class TestCase {
	
	public enum Status {
		SUCCESS, FAILURE
	}
	
	private String title;
	
	private Status status;
	
	private TestCase parent;
	
	private List<TestCase> children;
	
	private List<Object> adapters;
	
	private URI sourceUri;
	
	private Element inputElement;
	
	private Element logElement;
	
	public TestCase() {
		this("no name", Status.SUCCESS);
	}
	
	public TestCase(String title, Status status) {
		this(title, status, new ArrayList<TestCase>());
	}
	
	public TestCase(String title, Status status, List<TestCase> children) {
		this.title = title;
		this.status = status;
		this.children = children;
		adapters = Lists.newArrayList();
	}
	
	public void setInputElement(Element element) {
		this.inputElement = element;
	}
	
	public void setLogElement(Element element) {
		this.logElement = element;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setParent(TestCase parent) {
		this.parent = parent;
	}
	
	public void setSourceUri(URI uri) {
		this.sourceUri = uri;
	}
	
	public void addTestCase(TestCase testCase) {
		children.add(testCase);
	}
	
	public Element getLogElement() {
		return this.logElement;
	}
	
	public Element getInputElement() {
		return this.inputElement;
	}
	
	public URI getSourceUri() {
		return sourceUri;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public TestCase getParent() {
		return parent;
	}
	
	public List<TestCase> getChildren() {
		return children;
	}

	public Object getAdapter(Class<?> type) {
		for(Object object : adapters) {
			if(object.getClass() == type) {
				return object;
			}
		}
		return null;
	}
	
	public void addAdapter(Object adapter) {
		if(adapters == null) {
			adapters = Lists.newArrayListWithExpectedSize(5);
		}
		if(adapter != null) {
			adapters.add(adapter);			
		}
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("titel={");
		strBuffer.append(title);
		strBuffer.append("}");
		strBuffer.append("status={");
		strBuffer.append(status);
		strBuffer.append("}");
		return strBuffer.toString();
	}
}
