/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IContentProvider;
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ConfigScanView;
import org.vclipse.configscan.views.ContentProvider;
import org.vclipse.configscan.views.ErrorBasedContentProvider;
import org.vclipse.configscan.views.ErrorBasedContentProviderExtension;

public class ShowErrorBasedTreeAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + ShowErrorBasedTreeAction.class.getSimpleName();
	
	private ContentProvider contentProvider;
	
	private ErrorBasedContentProviderExtension errorBasedContentProvider;
	
	public ShowErrorBasedTreeAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper) {
		super(view, imageHelper, Action.AS_CHECK_BOX);
		setText("Show error based tree");
		setToolTipText("Show error based tree");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.ERROR_BASED_TREE));
		setId(ID);
		errorBasedContentProvider = new ErrorBasedContentProviderExtension();
	}

	@Override
	public void run() {
		if(isChecked()) {
			IContentProvider contentProvider = treeViewer.getContentProvider();
			if(contentProvider instanceof ContentProvider) {
				this.contentProvider = (ContentProvider)contentProvider;
			}
			treeViewer.setAutoExpandLevel(0);
			treeViewer.setContentProvider(errorBasedContentProvider);
		} else {
			IContentProvider contentProvider = treeViewer.getContentProvider();
			if(contentProvider instanceof ErrorBasedContentProvider) {
				errorBasedContentProvider = (ErrorBasedContentProviderExtension)contentProvider;
			}
			treeViewer.setAutoExpandLevel(2);			treeViewer.setContentProvider(this.contentProvider);
		}
		view.setInput(view.getInput());	
	}
}
