package org.vclipse.configscan;

import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestCase.Status;

import junit.framework.Assert;

public class TestCaseAssert extends Assert {

	public static void checkComplete(TestCase testCase, boolean noInputNoUri, boolean root, Class<?> hasType) {
		assertNotNull(testCase.getTitle());
		assertNotNull(testCase.getStatus());
		if(!root) {
			assertNotNull(testCase.getParent());
			assertTrue(testCase.getParent() instanceof TestGroup);
		}
		assertNotNull(testCase.getRoot());			
		assertNotNull(testCase.getLogElement());
		if(!noInputNoUri) {
			assertNotNull(testCase.getSourceURI());
			assertNotNull(testCase.getInputElement());			
		}
		assertTrue(testCase.getClass() == hasType);
	}
	
	public static void checkValues(TestCase testCase, String title, Status status, TestCase parent) {
		assertEquals("Names are equal", title, testCase.getTitle());
		assertEquals("Status is the same", status, testCase.getStatus());
		assertEquals("Parents are same", parent, testCase.getParent());
	}
}
