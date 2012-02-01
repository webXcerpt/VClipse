package org.vclipse.configscan.utils;

import java.util.List;

import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestCase.Status;

import com.google.common.collect.Lists;

public final class FailureTreeTraverser extends AbstractTreeTraverser<TestCase> {

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
}
