package org.vclipse.configscan.views.actions;

import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ConfigScanView;

public class ExpandTreeAction extends SimpleTreeViewerAction {

	public ExpandTreeAction(ConfigScanView view, ConfigScanImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Expand all");
		setToolTipText("Expand all");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.EXPAND_ALL));
	}
	
	@Override
	public void run() {
		treeViewer.expandAll();
	}
}
