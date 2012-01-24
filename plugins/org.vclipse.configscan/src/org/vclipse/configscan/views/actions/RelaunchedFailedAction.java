package org.vclipse.configscan.views.actions;

import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.model.TestRunAdapter;
import org.vclipse.configscan.views.ConfigScanView;

import com.google.inject.Provider;

public class RelaunchedFailedAction extends SimpleTreeViewerAction {

	public RelaunchedFailedAction(ConfigScanView view, ConfigScanImageHelper imageHelper, Provider<TestRunAdapter> testRunProvider) {
		super(view, imageHelper);
		setText("Relaunched failed tests");
		setToolTipText("Relaunch failed tests");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.RELAUNCHF));
	}

	@Override
	public void run() {
		
	}
}
