/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.ui.outline;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.xtext.ui.editor.outline.impl.OutlinePage;
import org.vclipse.base.ui.util.IExtendedImageHelper;
import org.vclipse.vcml.ui.IUiConstants;
import org.vclipse.vcml.ui.extension.IExtensionPointUtilities;
import org.vclipse.vcml.ui.outline.actions.VcmlOutlineAction;
import org.vclipse.vcml.utils.ISapConstants;

import com.google.inject.Inject;

public class VCMLOutlinePage extends OutlinePage implements IPropertyChangeListener {

	private static final String POPUP_MENU_ID = VCMLOutlinePage.class.getSimpleName().toLowerCase() + "menu";

	@Inject
	private IExtendedImageHelper imageHelper;
	
	@Inject
	private IPreferenceStore preferenceStore;
	
	@Inject
	private IExtensionPointUtilities extensionPointUtilities;

	private Menu menu;
	
	@Override
	public void dispose() {
		preferenceStore.removePropertyChangeListener(this);
		super.dispose();
	}
	
	public void propertyChange(final PropertyChangeEvent event) {
		String property = event.getProperty();
		if(IUiConstants.SAP_HIERARCHY_ACTIVATED.equals(property) || ISapConstants.DEFAULT_LANGUAGE.equals(property)) {
			getRefreshJob().schedule();
		}
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		preferenceStore.addPropertyChangeListener(this);
		TreeViewer treeViewer = getTreeViewer();
		IToolBarManager toolBarManager = getSite().getActionBars().getToolBarManager();
		
		createHierarchySwitchAction(toolBarManager);
		createPopupMenuAction(treeViewer, toolBarManager);
		createMenu(treeViewer);
	}

	private void createPopupMenuAction(TreeViewer treeViewer, IToolBarManager toolBarManager) {
		List<VcmlOutlineAction> actions = extensionPointUtilities.getActions();
		for(VcmlOutlineAction action : actions) {
			treeViewer.addSelectionChangedListener(action);
		}
	}

	private void createHierarchySwitchAction(IToolBarManager toolBarManager) {
		final ImageDescriptor sapImageDescriptor = imageHelper.getImageDescriptor(IUiConstants.SAP_HIERARCHY_IMAGE);
		final ImageDescriptor docImageDescriptior = imageHelper.getImageDescriptor(IUiConstants.DOC_HIERARCHY_IMAGE);
		
		// create an action for hierarchy switching
		final Action action = new Action() {
			@Override
			public void run() {
				setChecked(!isChecked());
				if(isChecked()) {
					setText("Document hierarchy");
					setToolTipText("Document hierarchy");
					setImageDescriptor(docImageDescriptior);
					preferenceStore.setValue(IUiConstants.SAP_HIERARCHY_ACTIVATED, false);	
				} else {
					setText("PMEVC-like hierarchy");
					setToolTipText("PMEVC-like hierarchy");
					setImageDescriptor(sapImageDescriptor);
					preferenceStore.setValue(IUiConstants.SAP_HIERARCHY_ACTIVATED, true);
				}
			}
		};
		
		// we begin with a false value for IUiConstants.SAP_HIERARCHY_ACTIVATED => see also PreferenceInitializer
		action.setText("PMEVC-like hierarchy");
		action.setToolTipText("PMEVC-like hierarchy");
		action.setImageDescriptor(sapImageDescriptor);
		toolBarManager.add(action);
	}
	
	private void createMenu(final TreeViewer treeViewer) {
		final MenuManager menuManager = new MenuManager(POPUP_MENU_ID);
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {	
			public void menuAboutToShow(final IMenuManager manager) {
				for(Map.Entry<VcmlOutlineAction, String> entry : extensionPointUtilities.getPathes().entrySet()) {
					GroupMarker groupMarker = new GroupMarker(entry.getValue());
					manager.add(groupMarker);
					manager.appendToGroup(groupMarker.getGroupName(), entry.getKey());
					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				}
			}
		});
		menu = menuManager.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(POPUP_MENU_ID, menuManager, treeViewer);
	}
}
