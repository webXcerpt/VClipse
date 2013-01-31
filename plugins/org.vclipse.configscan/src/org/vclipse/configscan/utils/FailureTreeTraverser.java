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
package org.vclipse.configscan.utils;

import java.util.List;
import java.util.Map;

import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestCase.Status;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class FailureTreeTraverser extends AbstractTreeTraverser<TestCase> {

	private Map<TestGroup, Integer> visited;
	
	public FailureTreeTraverser() {
		visited = Maps.newHashMap();
	}
	
	@Override
	public TestCase getNextItem(TestCase item) {
		visited.clear();
		return super.getNextItem(item);
	}
	
	@Override
	public TestCase getPreviousItem(TestCase item) {
		visited.clear();
		return super.getPreviousItem(item);
	}

	@Override
	protected TestCase getNextItem(TestCase item, int index) {
		if(!shouldVisit(item)) {
			return null;
		}
		return super.getNextItem(item, index);
	}
	
	@Override
	public TestCase getPreviousItem(TestCase item, int index) {
		if(!shouldVisit(item)) {
			return null;
		}
		return super.getPreviousItem(item, index);
	}

	@Override
	protected TestCase getParent(TestCase item) {
		return item == null ? null : item.getParent();
	}
	
	@Override
	protected List<TestCase> getChildren(TestCase item) {
		if(item instanceof TestGroup) {
			return ((TestGroup)item).getTestCases();
		}
		return Lists.newArrayList();
	}

	@Override
	protected boolean propertyTest(TestCase item) {
		if(getChildren(item).isEmpty()) {
			if(Status.FAILURE == item.getStatus()) {
				return true;
			}
		}
		return false;
	}
	
	private boolean shouldVisit(TestCase item) {
		if(item instanceof TestGroup) {
			TestGroup testGroup = (TestGroup)item;
			if(visited.containsKey(testGroup)) {
				int visitIndex = visited.get(testGroup);
				if(visitIndex > getChildren(item).size() + 1) {
					return false;
				}
				visited.put(testGroup, visitIndex + 1);
			} else {
				visited.put(testGroup, 1);
			}
		}
		return true;
	}
}
