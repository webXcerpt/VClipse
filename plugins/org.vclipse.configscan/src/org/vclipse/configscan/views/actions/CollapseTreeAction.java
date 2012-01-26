package org.vclipse.configscan.views.actions;

import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ConfigScanView;

public final class CollapseTreeAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + CollapseTreeAction.class.getSimpleName();
	
	public CollapseTreeAction(ConfigScanView view, ConfigScanImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Collapse all");
		setToolTipText("Collapse all");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.COLLAPSE_ALL));
		setId(ID);
	}
	
	public void run() {
		treeViewer.collapseAll();
		treeViewer.expandToLevel(IConfigScanConfiguration.DEFAULT_EXPAND_LEVEL);
	}
}
