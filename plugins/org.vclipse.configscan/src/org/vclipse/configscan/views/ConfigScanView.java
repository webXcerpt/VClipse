/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.configscan.views;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.vclipse.base.ui.ClasspathAwareImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.utils.TestCaseFactory;
import org.vclipse.configscan.views.JobAwareTreeViewer.ITreeViewerLockListener;
import org.vclipse.configscan.views.JobAwareTreeViewer.TreeViewerLockEvent;
import org.vclipse.configscan.views.actions.CollapseTreeAction;
import org.vclipse.configscan.views.actions.CompareAction;
import org.vclipse.configscan.views.actions.ExpandTreeAction;
import org.vclipse.configscan.views.actions.ImportExportAction;
import org.vclipse.configscan.views.actions.NextAction;
import org.vclipse.configscan.views.actions.PreviousAction;
import org.vclipse.configscan.views.actions.RelaunchAction;
import org.vclipse.configscan.views.actions.RelaunchedFailedAction;
import org.vclipse.configscan.views.actions.ShowErrorBasedTreeAction;
import org.vclipse.configscan.views.actions.ShowFailuresAction;
import org.vclipse.configscan.views.actions.ShowHistroyAction;
import org.vclipse.configscan.views.actions.ToggleLabelProviderAction;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

public final class ConfigScanView extends ViewPart {
	
	public static final String ID = "org.vclipse.configscan.ConfigScanView";
	
	class PreferenceStoreListener implements IPropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if(IConfigScanConfiguration.EXPAND_TREE_ON_INPUT.equals(event.getProperty())) {
				viewer.setAutoExpandLevel((Boolean)event.getNewValue() ? IConfigScanConfiguration.DEFAULT_EXPAND_LEVEL : 0);
			}
		}
	}
	
	class TreeViewerLockListener implements ITreeViewerLockListener {
		@Override
		public void available(TreeViewerLockEvent event) {
			enableActions();
		}

		@Override
		public void locked(TreeViewerLockEvent event) {
			
		}
	}

	private static final String CONTEXT_MENU_ID = "ConfigScanViewContextMenu";

	@Inject
	private ContentProvider contentProvider;
	
	@Inject
	private ExtensionsHandlingLabelProvider labelProvider;
	
	@Inject
	private IPreferenceStore preferenceStore;
	
	@Inject
	private ClasspathAwareImageHelper imageHelper;
	
	@Inject
	private DocumentUtility documentUtility;
	
	@Inject
	private TestRunsHistory history;
	
	@Inject
	private AbstractUIPlugin plugin;
	
	@Inject
	private TestCaseFactory testCaseFactoy;
	
	private PreferenceStoreListener propertyChangeListener;
	private TreeViewerLockListener treeViewerLockListener;
	
	private JobAwareTreeViewer viewer;
	private DefaultLabelProvider defaultLabelProvider;
	
	private ShowErrorBasedTreeAction showErrorBasedTreeAction;
	private ToggleLabelProviderAction toggleContent;
	private ExpandTreeAction expandTreeAction;
	private CollapseTreeAction collapseTreeAction;
	private RelaunchAction relaunchAction;
	private RelaunchedFailedAction relaunchFailedAction;
	private ShowFailuresAction showFailuresAction;
	private NextAction nextFailureAction;
	private PreviousAction prevFailureAction;
	private CompareAction compareAction;
	
	private Map<String, Action> id2Action;
 
	private ConfigScanViewInput input;
	
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	@Override
	public void dispose() {		
		try {
			history.save(plugin.getStateLocation().append(IConfigScanConfiguration.HISTORY_FILE_NAME).toString());
		} catch (IllegalStateException exception) {
			ConfigScanPlugin.log("Could not save history. " + exception.getMessage(), IStatus.ERROR);
		} catch (IOException exception) {
			ConfigScanPlugin.log("Could not save history. " + exception.getMessage(), IStatus.ERROR);
		}	
		preferenceStore.removePropertyChangeListener(history);
		preferenceStore.removePropertyChangeListener(propertyChangeListener);
		
		viewer.removeTreeViewerLockListener(history);
		viewer.removeTreeViewerLockListener(treeViewerLockListener);
		viewer.removeSelectionChangedListener(defaultLabelProvider);
		super.dispose();
	}

	public void createPartControl(Composite parent) {
		id2Action = Maps.newHashMap();
		parent.setLayout(new GridLayout());
		
		viewer = new JobAwareTreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		defaultLabelProvider = new DefaultLabelProvider(labelProvider, viewer);
		viewer.setLabelProvider(defaultLabelProvider);			
		viewer.addSelectionChangedListener(defaultLabelProvider);
		viewer.setContentProvider(contentProvider);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer.addTreeViewerLockListener(history);
		viewer.addTreeViewerLockListener(treeViewerLockListener = new TreeViewerLockListener());
		
		preferenceStore.addPropertyChangeListener(history);
		
		preferenceStore.addPropertyChangeListener(propertyChangeListener = new PreferenceStoreListener());
		if(preferenceStore.getBoolean(IConfigScanConfiguration.EXPAND_TREE_ON_INPUT)) {
			viewer.setAutoExpandLevel(10);			
		}
		
		ColumnViewerToolTipSupport.enableFor(viewer);
		
		contributeToActionBars();		
		createContextMenu();
		hookDoubleClickAction();
		
		try {
			history.load(plugin.getStateLocation().append(IConfigScanConfiguration.HISTORY_FILE_NAME).toString());
		} catch (FileNotFoundException exception) {
			ConfigScanPlugin.log("Could not load history. " + exception.getMessage(), IStatus.ERROR);
		} catch (IllegalStateException exception) {
			ConfigScanPlugin.log("Could not load history. " + exception.getMessage(), IStatus.ERROR);
		}
		disableActions();
	}

	public void setInput(ConfigScanViewInput input) {
		this.input = input;
		disableActions();
		viewer.setInput(input);
		enableActions();
	}

	public ConfigScanViewInput getInput() {
		return input;
	}
	
	public JobAwareTreeViewer getViewer() {
		return viewer;
	}
	
	protected void enableOrDisable(String id, boolean enable) {
		id2Action.get(id).setEnabled(enable);
	}
	
	protected void disableActions() {
		for(String id : id2Action.keySet()) {
			enableOrDisable(id, false);
		}
	}
	
	protected void enableActions() {
		ConfigScanViewInput input = (ConfigScanViewInput)viewer.getInput();
		if(input != null) {
			boolean allActionsAvailable = true;
			for(TestRun testRun : input.getTestRuns()) {
				EObject testModel = testRun.getTestModel();
				if(testModel == null) {
					allActionsAvailable = false;
					break;
				}
			}
			if(!allActionsAvailable) {
				enableOrDisable(ToggleLabelProviderAction.ID, false);
				enableOrDisable(CompareAction.ID, false);
				enableOrDisable(RelaunchedFailedAction.ID, false);
				enableOrDisable(RelaunchAction.ID, false);
				
				enableOrDisable(CollapseTreeAction.ID, true);
				enableOrDisable(ExpandTreeAction.ID, true);
				enableOrDisable(NextAction.ID, true);
				enableOrDisable(PreviousAction.ID, true);
				enableOrDisable(ShowFailuresAction.ID, true);
			} else {
				for(String id : id2Action.keySet()) {
					enableOrDisable(id, true);
				}
			}
		}
	}
	
	private void contributeToActionBars() {
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		
		toolBarManager.add(showErrorBasedTreeAction = new ShowErrorBasedTreeAction(this, imageHelper));
		id2Action.put(ShowErrorBasedTreeAction.ID, showErrorBasedTreeAction);
		
		toolBarManager.add(toggleContent = new ToggleLabelProviderAction(this, imageHelper));
		id2Action.put(ToggleLabelProviderAction.ID, toggleContent);
		
		toolBarManager.add(new Separator());
		
		toolBarManager.add(compareAction = new CompareAction(this, imageHelper));
		id2Action.put(CompareAction.ID, compareAction);
		
		toolBarManager.add(new Separator());
		
		toolBarManager.add(expandTreeAction = new ExpandTreeAction(this, imageHelper));
		id2Action.put(ExpandTreeAction.ID, expandTreeAction);
		
		toolBarManager.add(collapseTreeAction = new CollapseTreeAction(this, imageHelper));
		id2Action.put(CollapseTreeAction.ID, collapseTreeAction);
		
		toolBarManager.add(new Separator());
		
		toolBarManager.add(showFailuresAction = new ShowFailuresAction(this, imageHelper));
		id2Action.put(ShowFailuresAction.ID, showFailuresAction);
		
		toolBarManager.add(new Separator());
		
		toolBarManager.add(relaunchAction = new RelaunchAction(this, imageHelper));
		id2Action.put(RelaunchAction.ID, relaunchAction);
		
		toolBarManager.add(relaunchFailedAction = new RelaunchedFailedAction(this, imageHelper));
		id2Action.put(RelaunchedFailedAction.ID, relaunchFailedAction);
		
		toolBarManager.add(new Separator());
		
		toolBarManager.add(nextFailureAction = new NextAction(this, imageHelper));
		id2Action.put(NextAction.ID, nextFailureAction);
		
		toolBarManager.add(prevFailureAction = new PreviousAction(this, imageHelper));
		id2Action.put(PreviousAction.ID, prevFailureAction);
		
		toolBarManager.add(new Separator());
		
		toolBarManager.add(new ImportExportAction(this, imageHelper, documentUtility, testCaseFactoy));
		toolBarManager.add(new ShowHistroyAction(this, imageHelper, history));
	}
	
	private void createContextMenu() {
		MenuManager menuManager = new MenuManager(CONTEXT_MENU_ID);
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {	
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(expandTreeAction);
				manager.add(collapseTreeAction);
				manager.add(new Separator());
				manager.add(relaunchAction);
				manager.add(relaunchFailedAction);
			}
		});
		Control control = viewer.getControl();
		Menu menu = menuManager.createContextMenu(control);
		control.setMenu(menu);
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				Object selectedObject = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if(selectedObject instanceof TestCase) {
					TestCase testCase = (TestCase)selectedObject;
					URI testStatementUri = testCase.getSourceURI();
					if(testStatementUri != null) {
						Resource resource = new XtextResourceSet().getResource(testStatementUri, true);
						if(resource == null) {
							ConfigScanPlugin.log("Can not create resource for uri " + testStatementUri.toString(), IStatus.ERROR);
						}
						EObject associatedEObject = resource.getResourceSet().getEObject(testStatementUri, true);
						if(associatedEObject != null) {
							IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							try {
								Resource eResource = associatedEObject.eResource();
								IEditorPart openEditor = IDE.openEditor(activePage, ResourceUtil.getFile(eResource), true);
								if(openEditor instanceof XtextEditor) {
									INode node = NodeModelUtils.getNode(associatedEObject);
									((XtextEditor)openEditor).selectAndReveal(node.getOffset(), node.getLength());
								}
							} catch (PartInitException exception) {
								ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
							}
						}
					}
				}
			}
		});
	}
}