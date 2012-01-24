package org.vclipse.configscan.views.actions;

import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ConfigScanView;

public class RelaunchAction extends SimpleTreeViewerAction {

	public RelaunchAction(ConfigScanView view, ConfigScanImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Rerun tests");
		setToolTipText("Rerun tests");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.RELAUNCH));
	}

	@Override
	public void run() {
		
	}
}
