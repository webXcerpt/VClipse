package org.vclipse.configscan.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestRunAdapter;

public class NextFailureAction extends Action {

	public static final String ID = ConfigScanPlugin.ID + ".nextFailureAction";
	
	private TreeViewer treeViewer;
	
	public NextFailureAction(TreeViewer treeViewer, ConfigScanImageHelper imageHelper) {
		setId(ID);
		setText("Show next failure");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.SELECT_NEXT));
		setToolTipText("Jump to next failed test");
		this.treeViewer = treeViewer;
	}
	
	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		if(!selection.isEmpty()) {
			SwtTreeTraverser treeTraverser = new SwtTreeTraverser();
			Object firstSelected = selection.getFirstElement();
			if(firstSelected instanceof TestRunAdapter) {
				firstSelected = ((TestRunAdapter)firstSelected).getTestCase();
			}
			if(firstSelected instanceof TestCase) {
				TestCase selectedTestCase = (TestCase)firstSelected;
				TestCase nextNode = treeTraverser.getNextNode(selectedTestCase);
				if(nextNode != null) {
					treeViewer.setSelection(new StructuredSelection(nextNode));
				}
			}
		}
	}
}
