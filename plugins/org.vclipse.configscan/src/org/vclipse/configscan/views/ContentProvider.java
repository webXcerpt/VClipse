/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.configscan.views;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.DeferredTreeContentManager;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestRunAdapter;

import com.google.common.collect.Lists;

public class ContentProvider implements ITreeContentProvider {
	
	private DeferredTreeContentManager contentManager;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(viewer instanceof JobAwareTreeViewer) {
			contentManager = new DeferredTreeContentManager((TreeViewer)viewer);
			contentManager.addUpdateCompleteListener((JobAwareTreeViewer)viewer);
		}
	}
	
	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		if(child instanceof TestCase) {
			return ((TestCase)child).getParent();
		}
		return null;
	}

	public Object [] getChildren(Object parent) {
		if(parent instanceof ConfigScanViewInput) {
			List<TestRunAdapter> testCases = Lists.newArrayList();
			for(TestCase testCase : ((ConfigScanViewInput)parent).getTestCases()) {
				Object adapter = testCase.getAdapter(TestRunAdapter.class);
				if(adapter != null) {
					testCases.add((TestRunAdapter)adapter);
				}
			}
			if(testCases.isEmpty()) {
				return ((ConfigScanViewInput)parent).getTestCases().toArray();
			}
			return testCases.toArray();
		} else if(parent instanceof TestCase) {
			Object adapter = ((TestCase)parent).getAdapter(TestRunAdapter.class);
			if(adapter != null) {
				return ((TestRunAdapter)adapter).getChildren(null);				
			}
			return ((TestCase)parent).getChildren().toArray();
		} else if(parent instanceof TestRunAdapter) {
			return contentManager.getChildren((TestRunAdapter)parent);				
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if(parent instanceof TestCase) {
			Object adapter = ((TestCase)parent).getAdapter(TestRunAdapter.class);
			if(adapter != null) {
				return contentManager.mayHaveChildren((TestRunAdapter)adapter);				
			}
			return ((TestCase)parent).getChildren().size() > 0;
		} else if(parent instanceof TestRunAdapter) {
			return contentManager.mayHaveChildren(parent);
		}
		return false;
	}
	
	public void dispose() {
		
	}
}