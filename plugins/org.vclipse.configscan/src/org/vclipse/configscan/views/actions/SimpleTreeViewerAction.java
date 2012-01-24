package org.vclipse.configscan.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.views.ConfigScanView;

public abstract class SimpleTreeViewerAction extends Action {

	protected ConfigScanImageHelper imageHelper;
	
	protected TreeViewer treeViewer;
	
	protected ConfigScanView view;
	
	public SimpleTreeViewerAction(ConfigScanView view, ConfigScanImageHelper imageHelper, int style) {
		super("", style);
		this.imageHelper = imageHelper;
		this.treeViewer = view.getViewer();
		this.view = view;
	}
	
	public SimpleTreeViewerAction(ConfigScanView view, ConfigScanImageHelper imageHelper) {
		this.imageHelper = imageHelper;
		this.treeViewer = view.getViewer();
		this.view = view;
	}
}
