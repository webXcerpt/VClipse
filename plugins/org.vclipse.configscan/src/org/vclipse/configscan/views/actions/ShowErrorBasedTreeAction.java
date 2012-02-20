package org.vclipse.configscan.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IContentProvider;
import org.vclipse.base.ui.ClasspathAwareImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ConfigScanView;
import org.vclipse.configscan.views.ContentProvider;
import org.vclipse.configscan.views.ErrorBasedContentProvider;

public class ShowErrorBasedTreeAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + ShowErrorBasedTreeAction.class.getSimpleName();
	
	private ContentProvider contentProvider;
	
	private ErrorBasedContentProvider errorBasedContentProvider;
	
	public ShowErrorBasedTreeAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper) {
		super(view, imageHelper, Action.AS_CHECK_BOX);
		setText("Show error based tree");
		setToolTipText("Show error based tree");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.ERROR_BASED_TREE));
		setId(ID);
		errorBasedContentProvider = new ErrorBasedContentProvider();
	}

	@Override
	public void run() {
		if(isChecked()) {
			IContentProvider contentProvider = treeViewer.getContentProvider();
			if(contentProvider instanceof ContentProvider) {
				this.contentProvider = (ContentProvider)contentProvider;
			}
			treeViewer.setContentProvider(errorBasedContentProvider);
		} else {
			IContentProvider contentProvider = treeViewer.getContentProvider();
			if(contentProvider instanceof ErrorBasedContentProvider) {
				errorBasedContentProvider = (ErrorBasedContentProvider)contentProvider;
			}
			treeViewer.setContentProvider(this.contentProvider);		}
	}
}
