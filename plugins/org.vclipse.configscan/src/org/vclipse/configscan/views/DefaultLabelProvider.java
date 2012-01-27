package org.vclipse.configscan.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestRun;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DefaultLabelProvider extends DelegatingStyledCellLabelProvider implements ISelectionChangedListener {

	private static final String COLOR_NOT_MATCHING = "not_matching";
	private static final String COLOR_NOT_EXISTING = "not_existing";
	
	private static Boolean TEST_CASE_NOT_MATCHING = false;
	private static Boolean TEST_CASE_NOT_EXISTING = false;
	
	private IStyledLabelProvider labelProvider;
	
	private Map<String, Color> colorHash;
	
	private List<TestCase> searchHash;
	
	private boolean comparisonEnabled;
	
	private TestRun selectedTestRun;
	
	private TreeViewer treeViewer;
	
	public DefaultLabelProvider(IStyledLabelProvider labelProvider, TreeViewer treeViewer) {
		super(labelProvider);
		this.labelProvider = labelProvider;
		
		colorHash = Maps.newHashMap();
		searchHash = Lists.newArrayList();
		
		Display defaultDisplay = Display.getDefault();
		
		Color color = new Color(defaultDisplay, new RGB(244, 208, 161));
		colorHash.put(COLOR_NOT_MATCHING, color);
		color = new Color(defaultDisplay, new RGB(174, 161, 244));
		colorHash.put(COLOR_NOT_EXISTING, color);
		this.treeViewer = treeViewer;
	}
	
	private void fillSearchHash(List<TestCase> testCases) {
		for(TestCase testCase : testCases) {
			if(testCase instanceof TestGroup) {
				fillSearchHash(((TestGroup)testCase).getTestCases());
			}
			searchHash.add(testCase);
		}
	}

	public void enableComparison(boolean enabled) {
		comparisonEnabled = enabled;
		searchHash.clear();
	}
	
	@Override
	public String getToolTipText(Object element) {
		if(labelProvider instanceof CellLabelProvider) {
			return ((CellLabelProvider)labelProvider).getToolTipText(element);			
		}
		return "";
	}
	
	boolean isElementInComparisonTree(List<TestCase> testCases, TestCase testCase) {
		for(TestCase ctc : testCases) {
			if(ctc == testCase) {
				return true;
			}
			if(ctc instanceof TestGroup) {
				return isElementInComparisonTree(((TestGroup)ctc).getTestCases(), testCase);
			}
		}
		return false;
	}

	@Override
	public Color getBackground(Object element) {
		if(comparisonEnabled && selectedTestRun != null) {
			if(element instanceof TestRun) {
				return super.getBackground(element);
			} else if(element instanceof TestCase) {
				TestCase testCase = (TestCase)element;
				if(searchHash.contains(testCase)) {
					return super.getBackground(element);
				} else if(isElementInComparisonTree(selectedTestRun.getTestCases(), testCase)) {
					searchHash.add(testCase);
				} else {
					TreePath treePath = getTreePath(testCase, new ArrayList<TestCase>());
					Boolean result = lookForPath(treePath);
					if(result == TEST_CASE_NOT_MATCHING && !TEST_CASE_NOT_MATCHING) {
						return colorHash.get(COLOR_NOT_MATCHING);
					} else if(result == TEST_CASE_NOT_EXISTING && !TEST_CASE_NOT_EXISTING) {
						return colorHash.get(COLOR_NOT_EXISTING);
					}
				}
			}
		}
		return super.getBackground(element);
	}

	private TreePath getTreePath(TestCase testCase, List<TestCase> segments) {
		segments.add(testCase);
		TestCase parent = testCase.getParent();
		if(parent == null) {
			return new TreePath(/*Lists.reverse(segments)*/ segments.toArray());  // FIXME activate with Xtext 2.2
		} else {
			return getTreePath(parent, segments);
		}
	}
	
	private Boolean lookForPath(TreePath treePath) {
		for(int i=0; i<treePath.getSegmentCount(); i++) {
			Object segment = treePath.getSegment(i);
			if(searchHash.contains(segment)) {
				return true;
			} else if(segment instanceof TestGroup) {
				TestGroup testGroup = (TestGroup)segment;
				
			} else if(segment instanceof TestCase) {
				TestCase testCase = (TestCase)segment;
				
			}
		}
		return TEST_CASE_NOT_MATCHING;
	}
	
	@Override
	public void dispose() {
		for(String key : colorHash.keySet()) {
			colorHash.get(key).dispose();
		}
		colorHash.clear();
		super.dispose();
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		Object element = selection.getFirstElement();
		if(element instanceof TestRun) {
			selectedTestRun = (TestRun)element;
			searchHash.clear();
			searchHash.add(selectedTestRun);
			fillSearchHash(selectedTestRun.getTestCases());
			treeViewer.refresh(true);
		}
	}
}
