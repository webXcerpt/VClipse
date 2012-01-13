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
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.vclipse.configscan.implementation.ConfigScanTestCase;

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
		if(child instanceof ConfigScanTestCase) {
			return ((ConfigScanTestCase)child).getParent(null);
		}
		return null;
	}

	public Object [] getChildren(Object parent) {
		if(parent instanceof List<?>) {
			return ((List<?>)parent).toArray();
		} else if(parent instanceof IDeferredWorkbenchAdapter) {
			return contentManager.getChildren(parent);
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if(parent instanceof IDeferredWorkbenchAdapter) {
			return contentManager.mayHaveChildren(parent);
		}
		return false;
	}
	
	public void dispose() {
		
	}
}