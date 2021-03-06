/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan.views.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.views.ConfigScanView;
import org.vclipse.configscan.views.ConfigScanViewInput;

public abstract class SimpleTreeViewerAction extends Action {

	protected ClasspathAwareImageHelper imageHelper;
	
	protected TreeViewer treeViewer;
	
	protected ConfigScanView view;
	
	public SimpleTreeViewerAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper, int style) {
		super("", style);
		this.imageHelper = imageHelper;
		this.treeViewer = view.getViewer();
		this.view = view;
	}
	
	public SimpleTreeViewerAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper) {
		this.imageHelper = imageHelper;
		this.treeViewer = view.getViewer();
		this.view = view;
	}
	
	protected void setSelectionToFirstElement() {
		Object input = treeViewer.getInput();
		if(input instanceof ConfigScanViewInput) {
			List<TestRun> testRuns = ((ConfigScanViewInput)input).getTestRuns();
			if(!testRuns.isEmpty()) {
				treeViewer.setSelection(new StructuredSelection(testRuns.get(0)));
			}			
		}
	}
	
	protected boolean singleSelection() {
		ISelection selection = treeViewer.getSelection();
		if(selection instanceof IStructuredSelection) {
			return ((IStructuredSelection)selection).toArray().length > 1;
		}
		return false;
	}
	
	
}
