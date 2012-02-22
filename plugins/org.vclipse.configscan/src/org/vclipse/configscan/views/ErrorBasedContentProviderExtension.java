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
		for(TestRun testRun : input.getTestRuns()) {
			List<TestCase> testCases = Lists.newArrayList();
			testCases.add(testRun);
			visit(testCases);
			super.getElements(null);
		}
		return testGroup2TreePath.keySet().toArray();
	}

	@Override
	public TreePath[] getParents(Object element) {
		return new TreePath[0];
	}

	@Override
	public Object[] getChildren(TreePath parentPath) {
		int segmentCount = parentPath.getSegmentCount();
		if(segmentCount == 1) {
			Object lastSegment = parentPath.getLastSegment();
			if(lastSegment instanceof TestCase && !(lastSegment instanceof TestGroup)) {
				return super.getChildren(new TreePath(new Object[]{lastSegment}));
			}
			if(lastSegment instanceof TestGroup) {
				Iterable<TestCase> filter = Iterables.filter(((TestGroup)lastSegment).getTestCases(), new Predicate<TestCase>() {
					@Override
					public boolean apply(TestCase testCase) {
						return Status.FAILURE == testCase.getStatus();
					}
				});
				return Lists.newArrayList(filter).toArray();
			}
		} else if(segmentCount == 1 || segmentCount == 2) {
			return super.getChildren(new TreePath(new Object[]{parentPath.getLastSegment()}));
		} else if(segmentCount > 2) {
			return super.getChildren(parentPath);
		}
		return new Object[0];
	}

	@Override
	public boolean hasChildren(TreePath path) {
		return !(path.getLastSegment() instanceof TestRun);
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