package org.vclipse.configscan.views.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestRunAdapter;
import org.vclipse.configscan.utils.FailureTreeTraverser;

public class NextFailureAction extends SimpleTreeViewerAction {

	public NextFailureAction(TreeViewer treeViewer, ConfigScanImageHelper imageHelper) {
		super(treeViewer, imageHelper);
		setText("Show next failure");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.SELECT_NEXT));
		setToolTipText("Jump to next failed test");
	}
	
	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		if(!selection.isEmpty()) {
			Object firstSelected = selection.getFirstElement();
			if(firstSelected instanceof TestRunAdapter) {
				firstSelected = ((TestRunAdapter)firstSelected).getTestCase();
			}
			if(firstSelected instanceof TestCase) {
				TestCase nextNode = new FailureTreeTraverser().getNextItem((TestCase)firstSelected);
				if(nextNode != null) {
					treeViewer.setSelection(new StructuredSelection(nextNode));
				}
			}
		}
	}
}
