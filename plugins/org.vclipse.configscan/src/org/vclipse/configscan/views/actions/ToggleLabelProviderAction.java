package org.vclipse.configscan.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ExtensionsHandlingLabelProvider;

public class ToggleLabelProviderAction extends SimpleTreeViewerAction {

	public ToggleLabelProviderAction(TreeViewer treeViewer, ConfigScanImageHelper imageHelper) {
		super(treeViewer, imageHelper, Action.AS_CHECK_BOX);
		setText("Switch labels");
		setToolTipText("Display with labels from test language");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.LABEL_EXTENSION));
	}
	
	public void run() {
		DelegatingStyledCellLabelProvider delegatingProvider = (DelegatingStyledCellLabelProvider)treeViewer.getLabelProvider();
		ExtensionsHandlingLabelProvider labelProvider = (ExtensionsHandlingLabelProvider)delegatingProvider.getStyledStringProvider();
		if(isChecked()) {
			labelProvider.enableExtension(true);
			setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.FYSBEE));
			setToolTipText("Display with ConfigScan labels");
		} else {
			labelProvider.enableExtension(false);
			setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.LABEL_EXTENSION));
			setToolTipText("Display with labels from test language");
		}
		treeViewer.refresh(true);
		setEnabled(false);
	}
}