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
package org.vclipse.configscan.views;

import java.util.List;

import org.eclipse.jface.viewers.TreePath;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.vclipse.configscan.impl.model.TestCaseUtils;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestRun;

import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ErrorBasedContentProviderExtension extends ErrorBasedContentProvider {

	private HashMultimap<TestCase, TreePath> testGroup2TreePath;

	private TestCaseUtils testCaseUtils;
	
	public ErrorBasedContentProviderExtension() {
		testGroup2TreePath = HashMultimap.create();
		testCaseUtils = new TestCaseUtils();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		testGroup2TreePath.clear();
		for(TestCase testCase : input.getTestRuns()) {
			visit(Lists.newArrayList(testCase));
		}
		List<Object> elements = Lists.newArrayList();
		elements.addAll(Lists.newArrayList(testGroup2TreePath.keySet()));
		elements.addAll(Lists.newArrayList(super.getElements(inputElement)));
		return elements.toArray();
	}

	@Override
	public Object[] getChildren(TreePath parentPath) {
		int segmentCount = parentPath.getSegmentCount();
		Object lastSegment = parentPath.getLastSegment();
		if(segmentCount < 2) {
			if(lastSegment instanceof TestGroup) {
				Iterable<TestCase> filter = Iterables.filter(((TestGroup)lastSegment).getTestCases(), new Predicate<TestCase>() {
					@Override
					public boolean apply(TestCase testCase) {
						return Status.FAILURE == testCase.getStatus();
					}
				});
				return Lists.newArrayList(filter).toArray();
			} else if(!(lastSegment instanceof TestRun)) {
				return super.getChildren(new TreePath(new Object[]{lastSegment}));
			}
		} else if(segmentCount == 2) {
			if(lastSegment instanceof TestGroup) {
				return super.getChildren(parentPath);
			} else if(!(lastSegment instanceof TestRun)) {
				return super.getChildren(new TreePath(new Object[]{lastSegment}));
			}
		} else {
			return super.getChildren(parentPath);
		}
		return new Object[0];
	}

	@Override
	public void dispose() {
		testGroup2TreePath.clear();
	}

	private void visit(List<TestCase> testCases) {
		for(TestCase testCase : testCases) {
			if(testCase instanceof TestGroup) {
				TestGroup testGroup = (TestGroup)testCase;
				if(Status.FAILURE == testGroup.getStatus()) {
					if(testCaseUtils.isDomainTest(testGroup)) {
						testGroup2TreePath.put(testGroup, null);
					} 
				}
				visit(testGroup.getTestCases());
			}
		}
	}
}