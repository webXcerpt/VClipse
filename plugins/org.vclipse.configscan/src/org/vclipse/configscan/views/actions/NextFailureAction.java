package org.vclipse.configscan.views.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.utils.FailureTreeTraverser;
import org.vclipse.configscan.views.ConfigScanView;

public final class NextFailureAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + NextFailureAction.class.getSimpleName();
	
	public NextFailureAction(ConfigScanView view, ConfigScanImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Show next failure");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.SELECT_NEXT));
		setToolTipText("Jump to next failed test");
		setId(ID);
	}
	
	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		if(!selection.isEmpty()) {
			Object firstSelected = selection.getFirstElement();
			if(firstSelected instanceof TestCase) {
				TestCase nextNode = new FailureTreeTraverser().getNextItem((TestCase)firstSelected);
				if(nextNode != null) {
					treeViewer.setSelection(new StructuredSelection(nextNode));
				}
			}
		}
	}
}
