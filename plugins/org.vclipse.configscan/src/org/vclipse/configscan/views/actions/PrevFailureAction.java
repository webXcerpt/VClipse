package org.vclipse.configscan.views.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.utils.FailureTreeTraverser;
import org.vclipse.configscan.views.ConfigScanView;

public final class PrevFailureAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + PrevFailureAction.class.getSimpleName();
	
	public PrevFailureAction(ConfigScanView view, ConfigScanImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Show previous failure");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.SELECT_PREV));
		setToolTipText("Jump to previous failed test");
		setId(ID);
	}

	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		if(!selection.isEmpty()) {
			Object firstSelected = selection.getFirstElement();
			if(firstSelected instanceof TestCase) {
				TestCase nextNode = new FailureTreeTraverser().getPreviousItem((TestCase)firstSelected);
				if(nextNode != null) {
					treeViewer.setSelection(new StructuredSelection(nextNode));
				}
			}
		}
	}
}
