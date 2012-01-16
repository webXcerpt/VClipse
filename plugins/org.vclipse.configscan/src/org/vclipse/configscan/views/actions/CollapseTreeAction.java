package org.vclipse.configscan.views.actions;

import org.eclipse.jface.viewers.TreeViewer;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.IConfigScanImages;

public class CollapseTreeAction extends SimpleTreeViewerAction {

	public CollapseTreeAction(TreeViewer treeViewer, ConfigScanImageHelper imageHelper) {
		super(treeViewer, imageHelper);
		setText("Collapse all");
		setToolTipText("Collapse all");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.COLLAPSE_ALL));
	}
	
	public void run() {
		treeViewer.collapseAll();
		treeViewer.expandToLevel(IConfigScanConfiguration.DEFAULT_EXPAND_LEVEL);
	}
}
