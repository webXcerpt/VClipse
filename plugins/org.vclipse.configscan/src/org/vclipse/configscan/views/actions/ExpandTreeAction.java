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
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ConfigScanView;

public final class ExpandTreeAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + ExpandTreeAction.class.getSimpleName();
	
	public ExpandTreeAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Expand all");
		setToolTipText("Expand all");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.EXPAND_ALL));
		setId(ID);
	}
	
	@Override
	public void run() {
		treeViewer.expandAll();
	}
}
