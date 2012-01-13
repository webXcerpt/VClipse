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
import org.eclipse.swt.widgets.Tree;
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
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.implementation.ConfigScanTestCase;
import org.vclipse.configscan.implementation.ConfigScanTestRun;
import org.vclipse.configscan.utils.DocumentUtility;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.inject.Inject;

public final class ConfigScanView extends ViewPart {

	public static final String ID = "org.vclipse.configscan.ConfigScanView";
	
	@Inject
	private ConfigScanImageHelper imageHelper;

	@Inject
	private ContentProvider contentProvider;
	
	@Inject
	private LabelProvider labelProvider;
	
	@Inject
	private DocumentUtility documentUtility;
	
	@Inject
	private IPreferenceStore preferenceStore;
	
	private PropertyChangeListener propertyChangeListener;
	
	private JobAwareTreeViewer viewer;
	
	private Action failures;
	private Action nextFailure;
	private Action previousFailure;
	private Action collapseAll;
	private Action expandAll;
	private ToggleLabelProviderAction toggleContent;
	
	private Composite parent;
	private Labels labels;

	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	@Override
	public void dispose() {
		preferenceStore.removePropertyChangeListener(propertyChangeListener);
		super.dispose();
	}

	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(4, true));
		
		labels = new Labels(parent, 0, 0, 0, 0);
		
		viewer = new JobAwareTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		viewer.setLabelProvider(labelProvider);				
		viewer.setContentProvider(contentProvider);
		
		preferenceStore.addPropertyChangeListener(propertyChangeListener = new PropertyChangeListener(viewer));
		if(preferenceStore.getBoolean(ConfigScanConfiguration.EXPAND_TREE_ON_INPUT)) {
			viewer.setAutoExpandLevel(ConfigScanConfiguration.DEFAULT_EXPAND_LEVEL);			
		}
		new DrillDownAdapter(viewer);
		
		ColumnViewerToolTipSupport.enableFor(viewer);
		
		this.parent = parent;
		Tree tree = viewer.getTree();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		tree.setLayoutData(data);
	
		createActions();
		contributeToActionBars();		
		hookDoubleClickAction();
	}

	public void setInput(List<ConfigScanTestRun> testRuns) {
		toggleContent.setEnabled(false);
		viewer.setInput(testRuns);
	}
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager toolBarManager = bars.getToolBarManager();
		FileAction file = new FileAction(parent, viewer, labels, imageHelper);	
		toolBarManager.add(toggleContent);
		toolBarManager.add(new Separator());
		toolBarManager.add(expandAll);
		toolBarManager.add(collapseAll);
		toolBarManager.add(new Separator());
		toolBarManager.add(failures);
		toolBarManager.add(new Separator());
		toolBarManager.add(previousFailure);
		toolBarManager.add(nextFailure);
		toolBarManager.add(new Separator());
		toolBarManager.add(file);
	}

	private void createActions() {
		collapseAll = new Action() {
			public void run() {
				viewer.collapseAll();
				viewer.expandToLevel(ConfigScanConfiguration.DEFAULT_EXPAND_LEVEL);
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
				if(element instanceof ConfigScanTestCase) {
					return !documentUtility.hasSuccessStatus(((ConfigScanTestCase)element).getLogElement());
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
		
		nextFailure = new Action() {
			public void run() {
//				ISelection selection = viewer.getSelection();
//				IStructuredSelection ss = ((IStructuredSelection) selection);
//				Element lastSelected = ((Element) ss.getFirstElement());
//				Document logDocument = ((ContentProvider) viewer.getContentProvider()).getInput();
//				NodeList allElements = null;
//				if(logDocument != null) {
//					allElements = logDocument.getElementsByTagName(DocumentUtility.NODE_NAME_LOG_MSG);	
//				}
//				if(allElements != null) {
//					if(lastSelected == null) {
//						lastSelected = (Element) allElements.item(0);
//						ss = new StructuredSelection(lastSelected);
//						viewer.setSelection(ss);
//					}
//				} else {
//					return;
//				}
//				Element nextEl = null;
//				lastSelected = findNextDeepestChild(lastSelected);
//				for(int i = 0; i < allElements.getLength() - 1; i++) {
//					if(allElements.item(i).equals(lastSelected)) {
//						do {
//							nextEl = (Element)(allElements.item(++i)); 
//						} while(i < allElements.getLength() - 1 && !DocumentUtility.ATTRIBUTE_VALUE_E.equals(
//								nextEl.getAttribute(DocumentUtility.ATTRIBUTE_STATUS)));
//						break;
//					}
//				}
//				if(nextEl != null && DocumentUtility.ATTRIBUTE_VALUE_E.equals(DocumentUtility.ATTRIBUTE_STATUS)) {
//					StructuredSelection next = new StructuredSelection(nextEl);
//					viewer.setSelection(next);
//				}
//				else {
//					viewer.setSelection(ss);
//				}
			}
		};
		nextFailure.setText("Show next failure");
		nextFailure.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.SELECT_NEXT));
		nextFailure.setToolTipText("Jump to next failed test");

		previousFailure = new Action() {
			public void run() {
//				ISelection selection = viewer.getSelection();
//				IStructuredSelection ss = ((IStructuredSelection) selection);
//				Element lastSelected = ((Element) ss.getFirstElement());
//				
//				Document logDocument = ((ContentProvider) viewer.getContentProvider()).getInput();
//				NodeList allElements = null;
//				
//				if(logDocument != null) {
//					allElements = logDocument.getElementsByTagName(DocumentUtility.NODE_NAME_LOG_MSG);	
//				}
//				
//				if(allElements != null) {
//					if(lastSelected == null) {
//						lastSelected = (Element) allElements.item(0);
//						ss = new StructuredSelection(lastSelected);
//					}
//				}
//				else {
//					return;
//				}
//				
//				Element nextEl = null;
//				
//				lastSelected = findNextDeepestChild(lastSelected);
//				
//				for(int i = allElements.getLength() - 1; i > 0; i--) {
//					if(allElements.item(i).equals(lastSelected)) {
//						do {
//							
//							nextEl = (Element)(allElements.item(--i)); 
//						} while(i > 1 && !DocumentUtility.ATTRIBUTE_VALUE_E.equals(
//								nextEl.getAttribute(DocumentUtility.ATTRIBUTE_STATUS)));
//						break;
//					}
//
//				}
//				
//				if(nextEl != null && DocumentUtility.ATTRIBUTE_VALUE_E.equals(nextEl.getAttribute(DocumentUtility.ATTRIBUTE_STATUS))) {
//						StructuredSelection next = new StructuredSelection(nextEl);
//						viewer.setSelection(next);
//				}
//				else {
//					viewer.setSelection(ss);
//				}
			}
		};
		previousFailure.setText("Previous failure");
		previousFailure.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.SELECT_PREV));
		previousFailure.setToolTipText("Jump to previous failed test");
		
		toggleContent = new ToggleLabelProviderAction(viewer, imageHelper);
	}
	
	protected Element findNextDeepestChild(Element lastSelected) {
		if("3".equals(lastSelected.getAttribute(DocumentUtility.ATTRIBUTE_LEVEL))) {
			return lastSelected;
		}
		
		NodeList nodes = lastSelected.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextDeepestChild = findNextDeepestChild((Element) nodes.item(i));
				if(nextDeepestChild != null && "3".equals(nextDeepestChild.getAttribute(DocumentUtility.ATTRIBUTE_LEVEL))) {
					return nextDeepestChild;
				}
			}
		}
		// TODO: if-loop below is obviously not necessary
		if("2".equals(lastSelected.getAttribute(DocumentUtility.ATTRIBUTE_LEVEL))) {
			Node nextSibling = lastSelected.getNextSibling();
			while(nextSibling != null && nextSibling.getNodeType() != Node.ELEMENT_NODE) {
				nextSibling = nextSibling.getNextSibling();
			}
			if(nextSibling != null) {
				Element nextEl = (Element) nextSibling;
				return findNextDeepestChild(nextEl);
			}
			
		}
		return lastSelected;
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				Object selectedObject = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if(selectedObject instanceof ConfigScanTestCase) {
					ConfigScanTestCase testCase = (ConfigScanTestCase)selectedObject;
					URI testStatementUri = testCase.getTestStatementUri();
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

//public void setInput(Document xmlLogDoc, Document xmlInputDoc, Map<Element, Element> mapLogInput, final Map<Element, URI> inputToUri) {
//int runs = documentUtility.getNumberOfRuns(xmlLogDoc);
//int successes = documentUtility.getNumberOfSuccess(xmlLogDoc);
//int failures = documentUtility.getNumberOfFailure(xmlLogDoc);
//int time = 0;
//labels.updateLabels(runs, failures, successes, time);
//}