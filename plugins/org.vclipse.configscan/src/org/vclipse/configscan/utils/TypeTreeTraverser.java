package org.vclipse.configscan.utils;

import java.util.List;

import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;

import com.google.common.collect.Lists;

public final class TypeTreeTraverser extends AbstractTreeTraverser<TestCase> {

	private Class<?> targetType;
	
	public TypeTreeTraverser(Class<?> targetType) {
		this.targetType = targetType;
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
		return item.getClass() == targetType;
	}
}
