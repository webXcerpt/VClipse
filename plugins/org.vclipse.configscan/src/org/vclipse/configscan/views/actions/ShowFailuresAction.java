package org.vclipse.configscan.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestRunAdapter;
import org.vclipse.configscan.impl.model.TestCase.Status;

public class ShowFailuresAction extends SimpleTreeViewerAction {

	private ViewerFilter failureFilter;
	
	public ShowFailuresAction(TreeViewer treeViewer, ConfigScanImageHelper imageHelper) {
		super(treeViewer, imageHelper, Action.AS_CHECK_BOX);
		setText("Show only failures");
		setToolTipText("Show only failures");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.FAILURES));
		failureFilter = new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return element instanceof TestCase 
						&& ((TestCase)element).getAdapter(TestRunAdapter.class) == null 
							&& ((TestCase)element).getStatus() == Status.FAILURE;
			}
		};
	}
	
	@Override
	public void run() {
		if(isChecked()) {
			treeViewer.addFilter(failureFilter);
		} else {
			treeViewer.removeFilter(failureFilter);
		}
	}
}
