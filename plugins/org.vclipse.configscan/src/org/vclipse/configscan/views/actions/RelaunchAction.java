package org.vclipse.configscan.views.actions;

import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.views.ConfigScanView;
import org.vclipse.configscan.views.ConfigScanViewInput;

public class RelaunchAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + RelaunchAction.class.getSimpleName();
	
	public RelaunchAction(ConfigScanView view, ConfigScanImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Rerun tests");
		setToolTipText("Rerun tests");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.RELAUNCH));
		setId(ID);
	}

	@Override
	public void run() {
		Object object = treeViewer.getInput();
		if(object instanceof ConfigScanViewInput) {
			ConfigScanViewInput input = (ConfigScanViewInput)object;
			for(TestRun testRun : input.getTestRuns()) {
				testRun.removeTestCases();
			}
			view.setInput(input);
		}
	}
}
