/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan;

import java.util.List;

import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.vclipse.configscan.impl.model.TestRun;

import junit.framework.Assert;

public class TestCaseAssert extends Assert {

	public static void testComplete(TestCase testCase, boolean noInputNoUri, boolean root, Class<?> hasType) {
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
	
	public static void testValues(TestCase testCase, String title, Status status, TestCase parent) {
		assertEquals("Names are equal", title, testCase.getTitle());
		assertEquals("Status is the same", status, testCase.getStatus());
		assertEquals("Parents are same", parent, testCase.getParent());
	}
	
	public static void testValues(TestRun testRun, String testRunName, String sessionName, int numOfTestGroups) {
		assertEquals(testRunName, testRun.getTitle());
		List<TestCase> sessions = testRun.getTestCases();
		assertEquals(1, sessions.size());
		TestCase session = sessions.get(0);
		testComplete(session, true, false, TestGroup.class);
		assertEquals(sessionName, session.getTitle());
		List<TestCase> testCases = ((TestGroup)session).getTestCases();
		assertEquals(testCases.size(), numOfTestGroups);
	}
	
}
