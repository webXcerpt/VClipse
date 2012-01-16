package org.vclipse.configscan.views.actions;

import java.util.List;

import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestCase.Status;

public class SwtTreeTraverser {

	private boolean atLeastOneHit;
	
	public TestCase getNextNode(TestCase selected) {
		atLeastOneHit = propertyHit(selected);
		TestCase parent = selected.getParent();
		return getNextNode(selected, parent == null ? -1 : parent.getChildren().indexOf(selected));
	}
	
	public TestCase getPrevNode(TestCase selected) {
		atLeastOneHit = propertyHit(selected);
		TestCase parent = selected.getParent();
		return getPrevNode(parent, parent == null ? -1 : parent.getChildren().indexOf(selected));
	}
	
	protected TestCase getNextNode(TestCase testCase, int index) {
		if(testCase != null) {
			List<TestCase> children = testCase.getChildren();
			if(!children.isEmpty()) {
				for(int i=0, size=children.size(), last=size-1; i<size; i++) {
					TestCase child = children.get(i);
					if(i<=index) {
						continue;
					} else if(!child.getChildren().isEmpty()) {
						return getNextNode(child, index);
					} else if(i == last) {
						TestCase parent = testCase.getParent();
						int newIndex = parent.getChildren().indexOf(testCase);
						if(newIndex == parent.getChildren().size()-1) {
							newIndex = -1;
							if(!atLeastOneHit || parent.getParent() == null) {
								return null;
							}
						}
						return getNextNode(parent, newIndex);
					} else if(propertyHit(child)) {
						return child;
					}
				}
			} else {
				return getNextNode(testCase.getParent(), index);
			}
		}
		return null;
	}

	protected TestCase getPrevNode(TestCase testCase, int index) {
		if(testCase != null) {
			List<TestCase> children = testCase.getChildren();
			if(!children.isEmpty()) {
				for(int first=0, i=index; i>=first; i--) {
					TestCase child = children.get(i);
					if(!child.getChildren().isEmpty()) {
						return getPrevNode(child, child.getChildren().size() - 1);
					} else if(i>=index) {
						continue;
					} else if(propertyHit(child)) {
						return child;
					} else if(i == first) {
						TestCase parent = testCase.getParent();
						int newIndex = parent.getChildren().indexOf(testCase) - 1;
						if(newIndex == -1) {
							newIndex = parent.getChildren().size() - 1;
							if(!atLeastOneHit || parent.getParent() == null) {
								return null;
							}
						}
						return getPrevNode(parent, newIndex);
					} 
				}
			} else {
				return getPrevNode(testCase.getParent(), index);
			}
		}
		return null;
	}
	
	public boolean propertyHit(TestCase testCase) {
		if(Status.FAILURE == testCase.getStatus() && testCase.getChildren().isEmpty()) {
			atLeastOneHit = true;
			return true;
		}
		return false;
	}

	
}