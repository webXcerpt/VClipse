package org.vclipse.configscan.views.actions;

import org.eclipse.jface.viewers.TreeViewer;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;

public class ExpandTreeAction extends SimpleTreeViewerAction {

	public ExpandTreeAction(TreeViewer treeViewer, ConfigScanImageHelper imageHelper) {
		super(treeViewer, imageHelper);
		setText("Expand all");
		setToolTipText("Expand all");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.EXPAND_ALL));
	}
	
	@Override
	public void run() {
		treeViewer.expandAll();
	}
}
