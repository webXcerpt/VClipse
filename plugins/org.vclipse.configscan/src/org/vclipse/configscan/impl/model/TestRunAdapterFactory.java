package org.vclipse.configscan.impl.model;

public class TestRunAdapterFactory {

	private static TestRunAdapterFactory testRunAdapterFactory;
	
	public static TestRunAdapterFactory getDefault() {
		if(testRunAdapterFactory == null) {
			testRunAdapterFactory = new TestRunAdapterFactory();
		}
		return testRunAdapterFactory;
	}
	
	public void adapt(TestRunAdapter testRunAdapter, TestCase testCase) {
		if(testRunAdapter != null && testCase != null) {
			testRunAdapter.setTestCase(testCase);
			testCase.addAdapter(testRunAdapter);
		}
	}
}
