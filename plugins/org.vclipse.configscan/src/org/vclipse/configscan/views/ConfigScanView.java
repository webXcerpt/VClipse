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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
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
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestRunAdapter;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.utils.TestCaseUtility;
import org.vclipse.configscan.views.JobAwareTreeViewer.ITreeViewerLockListener;
import org.vclipse.configscan.views.JobAwareTreeViewer.TreeViewerLockEvent;
import org.vclipse.configscan.views.actions.CollapseTreeAction;
import org.vclipse.configscan.views.actions.ExpandTreeAction;
import org.vclipse.configscan.views.actions.ImportExportAction;
import org.vclipse.configscan.views.actions.NextFailureAction;
import org.vclipse.configscan.views.actions.PrevFailureAction;
import org.vclipse.configscan.views.actions.RelaunchAction;
import org.vclipse.configscan.views.actions.RelaunchedFailedAction;
import org.vclipse.configscan.views.actions.ShowFailuresAction;
import org.vclipse.configscan.views.actions.ShowHistroyAction;
import org.vclipse.configscan.views.actions.ToggleLabelProviderAction;

import com.google.inject.Inject;
import com.google.inject.Provider;

public final class ConfigScanView extends ViewPart {
	
	public static final String ID = "org.vclipse.configscan.ConfigScanView";
	
	class PreferenceStoreListener implements IPropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			String property = event.getProperty();
			Object object = event.getNewValue();
			if(IConfigScanConfiguration.EXPAND_TREE_ON_INPUT.equals(property)) {
				viewer.setAutoExpandLevel((Boolean)object ? IConfigScanConfiguration.DEFAULT_EXPAND_LEVEL : 0);
			} else if(IConfigScanConfiguration.SAVE_HISTORY.equals(property)) {
				showHistoryAction.setEnabled((Boolean)object);
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
	private ConfigScanImageHelper imageHelper;
	
	@Inject
	private DocumentUtility documentUtility;
	
	@Inject
	private TestCaseUtility testCaseUtility;
	
	@Inject
	private TestRunsHistory history;
	
	@Inject
	private Provider<TestRunAdapter> testRunProvider;
	
	
	private PreferenceStoreListener propertyChangeListener;
	private TreeViewerLockListener treeViewerLockListener;
	
	private JobAwareTreeViewer viewer;

	private ToggleLabelProviderAction toggleContent;
	private ShowHistroyAction showHistoryAction;
	private ExpandTreeAction expandTreeAction;
	private CollapseTreeAction collapseTreeAction;
	private RelaunchAction relaunchAction;
	private RelaunchedFailedAction relaunchFailedAction;
	private ShowFailuresAction showFailuresAction;
	private NextFailureAction nextFailureAction;
	private PrevFailureAction prevFailureAction;
	private ImportExportAction importExportAction;

	private ConfigScanViewInput input;
	
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	@Override
	public void dispose() {
		preferenceStore.removePropertyChangeListener(propertyChangeListener);
		if(preferenceStore.getBoolean(IConfigScanConfiguration.SAVE_HISTORY)) {
			history.save();			
		}
		viewer.removeTreeViewerLockListener(history);
		viewer.removeTreeViewerLockListener(treeViewerLockListener);
		super.dispose();
	}

	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		
		viewer = new JobAwareTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		viewer.setLabelProvider(new DefaultLabelProvider(labelProvider));				
		viewer.setContentProvider(contentProvider);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer.addTreeViewerLockListener(history);
		viewer.addTreeViewerLockListener(treeViewerLockListener = new TreeViewerLockListener());
		
		preferenceStore.addPropertyChangeListener(propertyChangeListener = new PreferenceStoreListener());
		if(preferenceStore.getBoolean(IConfigScanConfiguration.EXPAND_TREE_ON_INPUT)) {
			viewer.setAutoExpandLevel(IConfigScanConfiguration.DEFAULT_EXPAND_LEVEL);			
		}
		
		ColumnViewerToolTipSupport.enableFor(viewer);
		
		contributeToActionBars();		
		createContextMenu();
		hookDoubleClickAction();
		
		if(preferenceStore.getBoolean(IConfigScanConfiguration.SAVE_HISTORY)) {
			history.load();
		}
	}

	public void setInput(ConfigScanViewInput input) {
		this.input = input;
		disableActions();
		viewer.setInput(input);
	}

	public ConfigScanViewInput getInput() {
		return input;
	}
	
	public JobAwareTreeViewer getViewer() {
		return viewer;
	}
	
	private void contributeToActionBars() {
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		toolBarManager.add(toggleContent = new ToggleLabelProviderAction(this, imageHelper));
		toolBarManager.add(new Separator());
		toolBarManager.add(expandTreeAction = new ExpandTreeAction(this, imageHelper));
		toolBarManager.add(collapseTreeAction = new CollapseTreeAction(this, imageHelper));
		toolBarManager.add(new Separator());
		toolBarManager.add(showFailuresAction = new ShowFailuresAction(this, imageHelper));
		toolBarManager.add(new Separator());
		toolBarManager.add(relaunchAction = new RelaunchAction(this, imageHelper));
		toolBarManager.add(relaunchFailedAction = new RelaunchedFailedAction(this, imageHelper, testRunProvider));
		toolBarManager.add(new Separator());
		toolBarManager.add(nextFailureAction = new NextFailureAction(this, imageHelper));
		toolBarManager.add(prevFailureAction = new PrevFailureAction(this, imageHelper));
		toolBarManager.add(new Separator());
		toolBarManager.add(importExportAction = new ImportExportAction(this, imageHelper, documentUtility, testCaseUtility));
		toolBarManager.add(showHistoryAction = new ShowHistroyAction(this, imageHelper, history));
		disableActions();
	}
	
	private void disableActions() {
		relaunchAction.setEnabled(false);
		relaunchFailedAction.setEnabled(false);
		toggleContent.setEnabled(false);
		expandTreeAction.setEnabled(false);
		collapseTreeAction.setEnabled(false);
		showFailuresAction.setEnabled(false);
		nextFailureAction.setEnabled(false);
		prevFailureAction.setEnabled(false);
		importExportAction.setEnabled(false);
		showHistoryAction.setEnabled(false);
	}
	
	private void enableActions() {
		relaunchAction.setEnabled(true);
		relaunchFailedAction.setEnabled(true);
		toggleContent.setEnabled(true);
		expandTreeAction.setEnabled(true);
		collapseTreeAction.setEnabled(true);
		showFailuresAction.setEnabled(true);
		nextFailureAction.setEnabled(true);
		prevFailureAction.setEnabled(true);
		importExportAction.setEnabled(true);
		showHistoryAction.setEnabled(preferenceStore.getBoolean(IConfigScanConfiguration.SAVE_HISTORY));
	}
	
	private void createContextMenu() {
		MenuManager menuManager = new MenuManager(CONTEXT_MENU_ID);
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {	
			public void menuAboutToShow(IMenuManager manager) {
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
					URI testStatementUri = testCase.getSourceUri();
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