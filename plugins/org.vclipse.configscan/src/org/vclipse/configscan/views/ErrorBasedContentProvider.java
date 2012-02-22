package org.vclipse.configscan.views;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestRun;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;

public class ErrorBasedContentProvider implements ITreePathContentProvider {

	protected ConfigScanViewInput input;
	
	private HashMultimap<TestCase, TreePath> testCase2TreePath;
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(viewer instanceof JobAwareTreeViewer) {
			testCase2TreePath = HashMultimap.create();
			input = (ConfigScanViewInput)newInput;
		}
	}
	
	public Object[] getElements(Object inputElement) {
		testCase2TreePath.clear();
		for(TestRun testRun : input.getTestRuns()) {
			List<TestCase> testCases = Lists.newArrayList();
			testCases.add(testRun);
			visit(testCases);
		}
		return testCase2TreePath.keySet().toArray();
	}
	
	public TreePath[] getParents(Object element) {
		return new TreePath[0];
	}
	
	public Object[] getChildren(TreePath parentPath) {
		int segmentCount = parentPath.getSegmentCount();
		if(segmentCount == 1) {
			Object lastSegment = parentPath.getLastSegment();
			if(lastSegment instanceof TestCase) {
				Iterator<TreePath> iterator = testCase2TreePath.get((TestCase)lastSegment).iterator();
				List<TestCase> testCases = Lists.newArrayList();
				while(iterator.hasNext()) {
					testCases.add((TestCase)iterator.next().getFirstSegment());
				}
				return testCases.toArray();
			}
			return new Object[]{lastSegment};
		} else if(segmentCount > 1) {
			Object lastSegment = parentPath.getLastSegment();
			if(lastSegment instanceof TestCase) {
				TestCase parent = ((TestCase)lastSegment).getParent();
				if(parent == null) {
					return new Object[0];
				}
				return new Object[]{parent};
			}
		}
		return new Object[0];
	}
	
	public boolean hasChildren(TreePath path) {
		return !(path.getLastSegment() instanceof TestRun);
	}
	
	public void dispose() {
		testCase2TreePath.clear();
	}
	
	private void visit(List<TestCase> testCases) {
		for(TestCase testCase : testCases) {
			if(testCase instanceof TestGroup) {
				visit(((TestGroup)testCase).getTestCases());
			} else {
				if(Status.FAILURE == testCase.getStatus()) {
					if(testCase2TreePath.containsKey(testCase)) {
						List<TreePath> paths = Lists.newArrayList();
						paths.addAll(testCase2TreePath.get(testCase));
						paths.add(getNextPath(testCase));
						for(TreePath path : paths) {
							testCase2TreePath.put(testCase, path);							
						}
					} else {
						testCase2TreePath.put(testCase, getNextPath(testCase));
					}
				}
			}
		}
	}

	private TreePath getNextPath(TestCase testCase) {
		TestCase parent = testCase.getParent();
		return parent == null ? null : new TreePath(new Object[]{parent});
	}
}
