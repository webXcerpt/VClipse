package org.vclipse.configscan.impl.model;

public class TestCaseUtils {

	public boolean isDomainTest(TestCase testCase) {
		String title = testCase.getTitle();
		return title.startsWith("Check_Domain") && (title.contains("cstic:") || title.contains("not tested value:"));
	}
	
	public boolean isMultiValue(TestCase testCase) {
		// TODO implement
		return false;
	}
}
