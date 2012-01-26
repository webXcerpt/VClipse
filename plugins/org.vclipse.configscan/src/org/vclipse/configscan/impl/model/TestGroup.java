package org.vclipse.configscan.impl.model;

import java.util.List;

import com.google.common.collect.Lists;

public class TestGroup extends TestCase {

	private List<TestCase> testCases;
	
	public TestGroup(TestCase parent) {
		super(parent);
		testCases = Lists.newArrayList();
	}
	
	public List<TestCase> getTestCases() {
		return testCases;
	}
	
	public void addTestCase(TestCase testCase) {
		if(testCase != null) {
			testCases.add(testCase);
		}
	}
	
	public void removeTestCase(TestCase testCase) {
		if(testCase != null) {
			testCases.remove(testCase);
		}
	}
	
	public void removeTestCases() {
		testCases.clear();
	}
}
