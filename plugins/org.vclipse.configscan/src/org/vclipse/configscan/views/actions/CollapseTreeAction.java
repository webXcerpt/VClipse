package org.vclipse.configscan.views.actions;

import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ConfigScanView;

public class CollapseTreeAction extends SimpleTreeViewerAction {

	public CollapseTreeAction(ConfigScanView view, ConfigScanImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Collapse all");
		setToolTipText("Collapse all");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.COLLAPSE_ALL));
	}
	
	public void run() {
		treeViewer.collapseAll();
		treeViewer.expandToLevel(IConfigScanConfiguration.DEFAULT_EXPAND_LEVEL);
	}
}
