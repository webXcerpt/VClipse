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

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.vclipse.configscan.impl.model.TestRunAdapter;
import org.vclipse.configscan.utils.TestCaseUtility;
import org.vclipse.configscan.views.actions.ImportExportAction;

import com.google.inject.Inject;
import com.google.inject.Provider;

public final class ConfigScanView extends ViewPart {

	public static final String ID = "org.vclipse.configscan.ConfigScanView";
	
	@Inject
	private ConfigScanImageHelper imageHelper;

	@Inject
	private ContentProvider contentProvider;
	
	@Inject
	private LabelProvider labelProvider;
	
	@Inject
	private IPreferenceStore preferenceStore;
	
	@Inject
	private TestCaseUtility testCaseUtility;
	
	private PropertyChangeListener propertyChangeListener;
	
	private JobAwareTreeViewer viewer;
	
	private Action failures;
	private Action nextFailure;
	private Action previousFailure;
	private Action collapseAll;
	private Action expandAll;
	
	private ToggleLabelProviderAction toggleContent;
	
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	@Override
	public void dispose() {
		preferenceStore.removePropertyChangeListener(propertyChangeListener);
		super.dispose();
	}

	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		
		viewer = new JobAwareTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		viewer.setLabelProvider(labelProvider);				
		viewer.setContentProvider(contentProvider);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		preferenceStore.addPropertyChangeListener(propertyChangeListener = new PropertyChangeListener(viewer));
		if(preferenceStore.getBoolean(IConfigScanConfiguration.EXPAND_TREE_ON_INPUT)) {
			viewer.setAutoExpandLevel(IConfigScanConfiguration.DEFAULT_EXPAND_LEVEL);			
		}
		
		new DrillDownAdapter(viewer);
		ColumnViewerToolTipSupport.enableFor(viewer);
		
		createActions();
		contributeToActionBars();		
		hookDoubleClickAction();
	}

	public void setInput(List<TestCase> testCases) {
		// disable this action on input -> 
		// it will be enabled as soon as tree is constructed 
		toggleContent.setEnabled(false);
		
		viewer.setInput(testCases);
	}
	
	@Inject
	private Provider<ImportExportAction> fileActionProvider;
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager toolBarManager = bars.getToolBarManager();
		//FileAction file = new FileAction(parent, viewer, labels, imageHelper);	
		toolBarManager.add(toggleContent);
		toolBarManager.add(new Separator());
		toolBarManager.add(expandAll);
		toolBarManager.add(collapseAll);
		toolBarManager.add(new Separator());
		toolBarManager.add(failures);
		toolBarManager.add(new Separator());
		//toolBarManager.add(previousFailure);
		//toolBarManager.add(nextFailure);
		toolBarManager.add(new Separator());
		
		ImportExportAction fileAction = fileActionProvider.get();
		fileAction.setTreeViewer(viewer);
		toolBarManager.add(fileAction);
	}

	private void createActions() {
		collapseAll = new Action() {
			public void run() {
				viewer.collapseAll();
				viewer.expandToLevel(IConfigScanConfiguration.DEFAULT_EXPAND_LEVEL);
			}
		};
		collapseAll.setText("Collapse all");
		collapseAll.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.COLLAPSE_ALL));
		collapseAll.setToolTipText("Collapse all");
		
		expandAll = new Action() {
			public void run() {
				viewer.expandAll();
			}
		};
		expandAll.setText("Expand all");
		expandAll.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.EXPAND_ALL));
		expandAll.setToolTipText("Expand all");

		final ViewerFilter failureFilter = new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if(element instanceof TestCase && ((TestCase)element).getAdapter(TestRunAdapter.class) == null) {
					return ((TestCase)element).getStatus() == Status.FAILURE;
				}
				return true;
			}
		};
		failures = new Action("", IAction.AS_CHECK_BOX) {
			public void run() {
				if(isChecked()) {
					viewer.addFilter(failureFilter);
				} else {
					viewer.removeFilter(failureFilter);
				}
			}
		};
		failures.setToolTipText("Show only failures");
		failures.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.FAILURES));
		
//		nextFailure = new FindNextTestCaseAction(viewer, testCaseUtility);
//		nextFailure.setText("Show next failure");
//		nextFailure.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.SELECT_NEXT));
//		nextFailure.setToolTipText("Jump to next failed test");

//		previousFailure = new Action() {
//			
//			public void run() {
//			}
//		};
//		previousFailure.setText("Previous failure");
//		previousFailure.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.SELECT_PREV));
//		previousFailure.setToolTipText("Jump to previous failed test");
		
		toggleContent = new ToggleLabelProviderAction(viewer, imageHelper);
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