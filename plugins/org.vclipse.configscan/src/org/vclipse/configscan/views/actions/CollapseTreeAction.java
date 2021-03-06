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

import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ConfigScanView;

public final class CollapseTreeAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + CollapseTreeAction.class.getSimpleName();
	
	public CollapseTreeAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Collapse all");
		setToolTipText("Collapse all");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.COLLAPSE_ALL));
		setId(ID);
	}
	
	public void run() {
		treeViewer.collapseAll();
		treeViewer.expandToLevel(IConfigScanConfiguration.DEFAULT_EXPAND_LEVEL);
	}
}
