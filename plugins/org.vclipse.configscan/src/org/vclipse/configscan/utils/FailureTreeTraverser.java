package org.vclipse.configscan.utils;

import java.util.List;

import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestCase.Status;

public class FailureTreeTraverser extends AbstractTreeTraverser<TestCase> {

	@Override
	protected boolean propertyHit(TestCase item) {
		if(Status.FAILURE == item.getStatus() && item.getChildren().isEmpty()) {
			atLeastOneHit = true;
			return true;
		}
		return false;
	}
	
	@Override
	protected TestCase getParent(TestCase item) {
		return item.getParent();
	}
	
	@Override
	protected List<TestCase> getChildren(TestCase item) {
		return item.getChildren();
	}
}
