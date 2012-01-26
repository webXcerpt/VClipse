package org.vclipse.configscan.views.actions;

import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.views.ConfigScanView;
import org.vclipse.configscan.views.ConfigScanViewInput;

public class RelaunchAction extends SimpleTreeViewerAction {

	public RelaunchAction(ConfigScanView view, ConfigScanImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Rerun tests");
		setToolTipText("Rerun tests");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.RELAUNCH));
	}

	@Override
	public void run() {
		Object object = treeViewer.getInput();
		if(object instanceof ConfigScanViewInput) {
			ConfigScanViewInput input = (ConfigScanViewInput)object;
			for(TestRun testRun : input.getTestRuns()) {
//				Object adapter = testCase.getAdapter(TestRunAdapter.class);
//				if(adapter != null) {
//					TestRunAdapter testRunAdapter = (TestRunAdapter)adapter;
//					testRunAdapter.getTestCase().clearChildren();
//				}
			}
			treeViewer.setInput(input);
		}
	}
}
