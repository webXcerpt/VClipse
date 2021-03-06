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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.vclipse.configscan.views.ConfigScanView;

public final class ShowFailuresAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + ShowFailuresAction.class.getSimpleName();
	
	private ViewerFilter failureFilter;
	
	public ShowFailuresAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper) {
		super(view, imageHelper, Action.AS_CHECK_BOX);
		setText("Show only failures");
		setToolTipText("Show only failures");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.FAILURES));
		failureFilter = new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if(element instanceof TestCase) {
					return ((TestCase)element).getStatus() == Status.FAILURE;
				}
				return true;
			}
		};
		setId(ID);
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
