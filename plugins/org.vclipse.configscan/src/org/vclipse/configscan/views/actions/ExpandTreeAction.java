package org.vclipse.configscan.views.actions;

import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ConfigScanView;

public final class ExpandTreeAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + ExpandTreeAction.class.getSimpleName();
	
	public ExpandTreeAction(ConfigScanView view, ConfigScanImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Expand all");
		setToolTipText("Expand all");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.EXPAND_ALL));
		setId(ID);
	}
	
	@Override
	public void run() {
		treeViewer.expandAll();
	}
}
