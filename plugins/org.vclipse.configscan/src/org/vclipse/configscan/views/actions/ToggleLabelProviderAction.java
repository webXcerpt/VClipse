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
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ConfigScanView;
import org.vclipse.configscan.views.labeling.ExtensionsHandlingLabelProvider;

public final class ToggleLabelProviderAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + ToggleLabelProviderAction.class.getSimpleName();
	
	public ToggleLabelProviderAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper) {
		super(view, imageHelper, Action.AS_CHECK_BOX);
		setText("Switch labels");
		setToolTipText("Display with labels from test language");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.LABEL_EXTENSION));
		setId(ID);
	}
	
	public void run() {
		DelegatingStyledCellLabelProvider delegatingProvider = (DelegatingStyledCellLabelProvider)treeViewer.getLabelProvider();
		ExtensionsHandlingLabelProvider labelProvider = (ExtensionsHandlingLabelProvider)delegatingProvider.getStyledStringProvider();
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
	}
}