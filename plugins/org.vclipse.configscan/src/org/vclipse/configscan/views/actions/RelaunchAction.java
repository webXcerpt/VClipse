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
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.views.ConfigScanView;
import org.vclipse.configscan.views.ConfigScanViewInput;

public class RelaunchAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + RelaunchAction.class.getSimpleName();
	
	public RelaunchAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Rerun tests");
		setToolTipText("Rerun tests");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.RELAUNCH));
		setId(ID);
	}

	@Override
	public void run() {
		Object object = treeViewer.getInput();
		if(object instanceof ConfigScanViewInput) {
			ConfigScanViewInput input = (ConfigScanViewInput)object;
			for(TestRun testRun : input.getTestRuns()) {
				testRun.removeTestCases();
			}
			view.setInput(input);
		}
	}
}
