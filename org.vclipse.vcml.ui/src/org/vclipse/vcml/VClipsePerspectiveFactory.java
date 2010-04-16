/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
package org.vclipse.vcml;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.wizards.newresource.BasicNewFolderResourceWizard;

/**
 * 
 */
public class VClipsePerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = VCMLPlugin.ID + ".perspective";
	
	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		// Project explorer on the left side
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.20f, editorArea);
		left.addView(IPageLayout.ID_PROJECT_EXPLORER);
		
		// Outline view on the right side
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.80f, editorArea);
		right.addView(IPageLayout.ID_OUTLINE);
		layout.addPerspectiveShortcut(ID);
		
		layout.addNewWizardShortcut(BasicNewFolderResourceWizard.WIZARD_ID);
		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f, layout.getEditorArea());
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottom.addView(IPageLayout.ID_PROGRESS_VIEW);
		
		// add to "Open Perspective" menu
		layout.addPerspectiveShortcut("org.eclipse.ui.resourcePerspective");
		layout.addPerspectiveShortcut(ID);
		
		// add to "Show view" menu
		//layout.addShowViewShortcut(VCMLView.VCML_VIEW_ID);
		layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
		layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
	}
}
