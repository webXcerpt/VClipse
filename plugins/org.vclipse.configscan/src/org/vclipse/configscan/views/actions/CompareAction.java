package org.vclipse.configscan.views.actions;

import org.eclipse.jface.action.Action;
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ConfigScanView;
import org.vclipse.configscan.views.labeling.DefaultLabelProvider;

public class CompareAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + CompareAction.class.getSimpleName();
	
	public CompareAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper) {
		super(view, imageHelper, Action.AS_CHECK_BOX);
		setText("Enable tests comparison");
		setToolTipText("Enable tests comparison");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.COMPARE));
		setId(ID);
	}

	@Override
	public void run() {
		DefaultLabelProvider labelProvider = (DefaultLabelProvider)treeViewer.getLabelProvider();
		if(isChecked()) {
			labelProvider.enableComparison(true);
		} else {
			labelProvider.enableComparison(false);
		}
	}
}
