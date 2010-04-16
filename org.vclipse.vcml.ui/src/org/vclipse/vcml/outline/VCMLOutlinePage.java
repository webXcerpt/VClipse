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
package org.vclipse.vcml.outline;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.linking.ILinker;
import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.ui.common.editor.outline.XtextContentOutlinePage;
import org.vclipse.vcml.VCMLPlugin;
import org.vclipse.vcml.VCMLUiPlugin;
import org.vclipse.vcml.outline.actions.HierarchySwitchAction;
import org.vclipse.vcml.outline.actions.IVCMLOutlineActionHandler;
import org.vclipse.vcml.outline.actions.VCMLOutlineAction;
import org.vclipse.vcml.utils.ISapConstants;

import com.google.inject.Inject;

/**
 * 
 */
public class VCMLOutlinePage extends XtextContentOutlinePage implements IPreferenceChangeListener {

	@Inject
	private IResourceFactory resourceFactory;
	
	@Inject
	private ILinker linker;
	
	@Inject
	private ILinkingService linkingService;
	
	@Inject
	private IGrammarAccess grammarAccess;
	
	/**
	 * 
	 */
	private static final String extensionPointID = VCMLUiPlugin.ID + ".outlinePageActions";
	
	/**
	 * 
	 */
	private static final String POPUP_MENU_ID = VCMLUiPlugin.ID + ".outlinePopupMenu";
	
	/**
	 * 
	 */
	private static final String ELEMENT_ACTION = "action";
	
	/**
	 * 
	 */
	private static final String ELEMENT_HANDLER = "handler";
	
	/**
	 * Action element attributes
	 */
	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_LABEL = "label";
	private static final String ATTRIBUTE_TOOTLTIP = "tooltip";
	private static final String ATTRIBUTE_ICON = "icon";
	private static final String ATTRIBUTE_DISABLED_ICON = "disabledIcon";
	private static final String ATTRIBUTE_STATE = "state";
	private static final String ATTRIBUTE_TOOLBAR_PATH = "toolbarPath";
	private static final String ATTRIBUTE_MENUBAR_PATH = "menubarPath";
	
	
	/**
	 * Handler element attributes
	 */
	private static final String ATTRIBUTE_TYPE = "type";
	private static final String ATTRIBUTE_HANDLER = "handler";
	private static final String ATTRIBUTE_ACTION = "action";
	
	/**
	 * 
	 */
	private final Map<String, VCMLOutlineAction> id2Action;
	
	/**
	 * is used to add actions to the menu
	 */
	private final Map<VCMLOutlineAction, String> action2Path;
	
	/**
	 * 
	 */
	private Menu menu;
	
	/**
	 * 
	 */
	public VCMLOutlinePage() {
		id2Action = new HashMap<String, VCMLOutlineAction>();
		action2Path = new LinkedHashMap<VCMLOutlineAction, String>(); // LinkedHashMap to guarantee order in menu
		 new InstanceScope().getNode(VCMLPlugin.ID).addPreferenceChangeListener(this);
	}

	/**
	 * @see org.eclipse.xtext.ui.common.editor.outline.XtextContentOutlinePage#dispose()
	 */
	@Override
	public void dispose() {
		new InstanceScope().getNode(VCMLPlugin.ID).removePreferenceChangeListener(this);
		super.dispose();
	}

	/**
	 * @see org.eclipse.xtext.ui.common.editor.outline.XtextContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		final TreeViewer treeViewer = getTreeViewer();
		IActionBars actionBars = getSite().getActionBars();
		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		
		// add action for tree view change
		toolBarManager.add(new HierarchySwitchAction(treeViewer));
		
		// Add contributing actions
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(extensionPointID);
		for(IExtension extension : point.getExtensions()) {
			String contributingPluginID = extension.getNamespaceIdentifier();
			for(IConfigurationElement element : extension.getConfigurationElements()) {
				String name = element.getName();
				if(ELEMENT_ACTION.equals(name)) {
					VCMLOutlineAction action = new VCMLOutlineAction(resourceFactory, this, linker, linkingService, grammarAccess);
					String id = element.getAttribute(ATTRIBUTE_ID);
					if(id != null) {
						action.setId(id);
						id2Action.put(id, action);
					}
					String label = element.getAttribute(ATTRIBUTE_LABEL);
					if(label != null) {
						action.setText(label);
					}
					String tooltip = element.getAttribute(ATTRIBUTE_TOOTLTIP);
					if(tooltip != null) {
						action.setToolTipText(tooltip);
					}
					String icon = element.getAttribute(ATTRIBUTE_ICON);
					if(icon != null) {
						ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin(contributingPluginID, icon);
						if(image != null) {
							action.setImageDescriptor(image);
						}
					}
					String disabledIcon = element.getAttribute(ATTRIBUTE_DISABLED_ICON);
					if(disabledIcon != null) {
						ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin(contributingPluginID, disabledIcon);
						if(image != null) {
							action.setDisabledImageDescriptor(image);
						}
					}
					String state = element.getAttribute(ATTRIBUTE_STATE);
					if(state != null) {
						action.setEnabled(Boolean.parseBoolean(state));
					}
					treeViewer.addSelectionChangedListener(action);
					
					String menubarPath = element.getAttribute(ATTRIBUTE_MENUBAR_PATH);
					if(menubarPath != null) {
						if(menu == null) {
							createMenu(treeViewer);
						}
						action2Path.put(action, menubarPath);
					}
					
					String toolbarPath = element.getAttribute(ATTRIBUTE_TOOLBAR_PATH);
					if(toolbarPath != null) {
						toolBarManager.add(action);
					}
				}
				if(ELEMENT_HANDLER.equals(name)) {
					String actionId = element.getAttribute(ATTRIBUTE_ACTION);
					if(actionId != null) {
						VCMLOutlineAction starter = id2Action.get(actionId);
						if(starter != null) {
							try {
								Object handler = element.createExecutableExtension(ATTRIBUTE_HANDLER);
								if(handler instanceof IVCMLOutlineActionHandler<?>) {
									IVCMLOutlineActionHandler<?> actionHandler = (IVCMLOutlineActionHandler<?>)handler;
									String type = element.getAttribute(ATTRIBUTE_TYPE);
									if(type != null) {			
										starter.addHandler(type, actionHandler);
									}
								}
							} catch(CoreException e) {
								System.out.println(e.getMessage());
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * @param treeViewer
	 */
	private void createMenu(TreeViewer treeViewer) {
		MenuManager menuManager = new MenuManager(POPUP_MENU_ID);
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {	
			public void menuAboutToShow(IMenuManager manager) {
				for(Map.Entry<VCMLOutlineAction,String> entry : action2Path.entrySet()) {
					GroupMarker groupMarker = new GroupMarker(entry.getValue());
					manager.add(groupMarker);
					manager.appendToGroup(groupMarker.getGroupName(), entry.getKey());
				}
			}
		});
		menu = menuManager.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(POPUP_MENU_ID, menuManager, treeViewer);
	}

	/**
	 * @see org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener#preferenceChange(org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent)
	 */
	public void preferenceChange(PreferenceChangeEvent event) {
		if(ISapConstants.DEFAULT_LANGUAGE.equals(event.getKey())) {
			TreeViewer treeViewer = getTreeViewer();
			treeViewer.setInput(treeViewer.getInput());
		}
	}
}
