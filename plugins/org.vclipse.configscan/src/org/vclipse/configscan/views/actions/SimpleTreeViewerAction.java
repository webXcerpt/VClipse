package org.vclipse.configscan.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.vclipse.configscan.ConfigScanImageHelper;

public abstract class SimpleTreeViewerAction extends Action {

	protected ConfigScanImageHelper imageHelper;
	
	protected TreeViewer treeViewer;
	
	public SimpleTreeViewerAction(TreeViewer treeViewer, ConfigScanImageHelper imageHelper, int style) {
		super("", style);
		this.imageHelper = imageHelper;
		this.treeViewer = treeViewer;
	}
	
	public SimpleTreeViewerAction(TreeViewer treeViewer, ConfigScanImageHelper imageHelper) {
		this.imageHelper = imageHelper;
		this.treeViewer = treeViewer;
	}
	
	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}
}
