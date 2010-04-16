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
package org.vclipse.vcml.outline.actions;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.vclipse.vcml.IUiConstants;
import org.vclipse.vcml.VCMLUiPlugin;


/**
 * 
 */
public class HierarchySwitchAction extends Action {
	
	/**
	 * 
	 */
	private final TreeViewer treeViewer;
	
	/**
	 * 
	 */
	private static final ImageDescriptor sapHierarchyImage = VCMLUiPlugin.getImageDescriptor(IUiConstants.SAP_HIERARCHY_IMAGE);
	
	/**
	 * 
	 */
	private static final ImageDescriptor docHierarchyImage = VCMLUiPlugin.getImageDescriptor(IUiConstants.DOC_HIERARCHY_IMAGE);
	
	/**
	 * 
	 */
	private static final IEclipsePreferences preferences = new InstanceScope().getNode(VCMLUiPlugin.ID);
	
	/**
	 * 
	 */
	public HierarchySwitchAction(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
		switchAction(preferences.getBoolean(IUiConstants.SAP_HIERARCHY_ACTIVATED, false));
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		preferences.flush();
		super.finalize();
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		switchAction(isChecked());
		treeViewer.setInput(treeViewer.getInput());
	}
	
	/**
	 * @param checked
	 */
	private void switchAction(boolean checked) {
		setChecked(checked);
		if(checked) {
			setText("PMEVC-like hierarchy");
			setToolTipText("PMEVC-like hierarchy");
			setImageDescriptor(sapHierarchyImage);
			preferences.putBoolean(IUiConstants.SAP_HIERARCHY_ACTIVATED, true);
		} else {
			setText("Document hierarchy");
			setToolTipText("Document hierarchy");
			setImageDescriptor(docHierarchyImage);
			preferences.putBoolean(IUiConstants.SAP_HIERARCHY_ACTIVATED, false);
		}
	}
}
