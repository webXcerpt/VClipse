package org.vclipse.configscan.views;

import org.eclipse.jface.action.Action;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.JobAwareTreeViewer.ITreeViewerLockListener;
import org.vclipse.configscan.views.JobAwareTreeViewer.TreeViewerLockEvent;

class ToggleLabelProviderAction extends Action implements ITreeViewerLockListener {

	private JobAwareTreeViewer treeViewer;
	
	private ConfigScanImageHelper imageHelper;
	
	public ToggleLabelProviderAction(JobAwareTreeViewer treeViewer, ConfigScanImageHelper imageHelper) {
		super("", Action.AS_CHECK_BOX);
		this.treeViewer = treeViewer;
		this.imageHelper = imageHelper;
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.LABEL_EXTENSION));
		setToolTipText("Display with labels from test language");
		treeViewer.addTreeViewerLockListener(this);
		setEnabled(false);
	}
	
	public void run() {
		LabelProvider labelProvider = (LabelProvider)treeViewer.getLabelProvider();
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

	@Override
	public void available(TreeViewerLockEvent event) {
		setEnabled(true);
	}

	@Override
	public void locked(TreeViewerLockEvent event) {
		
	}
	
}