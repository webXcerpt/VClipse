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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.DeferredTreeContentManager;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestRun;

public class ContentProvider implements ITreeContentProvider {
	
	private DeferredTreeContentManager contentManager;
	
	private JobAwareTreeViewer treeViewer;
	
	private ConfigScanViewInput input;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(viewer instanceof JobAwareTreeViewer) {
			treeViewer = (JobAwareTreeViewer)viewer;
			if(contentManager == null) {
				contentManager = new DeferredTreeContentManager(treeViewer);
				contentManager.addUpdateCompleteListener(treeViewer);				
			}
			input = (ConfigScanViewInput)newInput;
		}
	}
	
	public Object[] getElements(Object parent) {
		return input.getTestRuns().toArray();
	}

	public Object getParent(Object child) {
		if(child instanceof TestCase) {
			return ((TestCase)child).getParent();
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		if(parent instanceof IDeferredWorkbenchAdapter) {
			return contentManager.getChildren((TestRun)parent);				
		} else if(parent instanceof TestGroup) {
			return ((TestGroup)parent).getTestCases().toArray();
		} 
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if(parent instanceof TestRun) {
			return true;
		} else {
			return parent instanceof TestGroup;
		}
	}
	
	public void dispose() {
		contentManager.removeUpdateCompleteListener(treeViewer);
	}
}