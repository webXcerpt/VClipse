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

import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.eclipse.xtext.util.Pair;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.IConfigScanLabelProvider;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.extension.ExtensionPointReader;
import org.vclipse.configscan.utils.DocumentUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.inject.Inject;

public final class ConfigScanView extends ViewPart {

	public static final String ID = "org.vclipse.configscan.ConfigScanView";
	
	@Inject
	private ConfigScanImageHelper imageHelper;

	@Inject
	private ExtensionPointReader extensionPointReader;
	
	@Inject
	private ContentProvider contentProvider;
	
	@Inject
	private LabelProvider labelProvider;
	
	@Inject
	private DocumentUtility documentUtility;
	
	private TreeViewer viewer;
	
	private Action failures;
	private Action nextFailure;
	private Action previousFailure;
	private Action collapseAll;
	private Action expandAll;
	private Action toggleContent;
	
	private Composite parent;
	private Labels labels;

	private String fileExtension;
	
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	public void createPartControl(Composite parent) {
		GridLayout gridLayout = new GridLayout(4, true);
		parent.setLayout(gridLayout);
		
		labels = new Labels(parent, 0, 0, 0, 0);
		
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setAutoExpandLevel(ConfigScanConfiguration.EXPAND_LEVEL);
		viewer.setLabelProvider(labelProvider);		// at initialization we have no map			
		viewer.setContentProvider(contentProvider);
		
		ViewerFilter removeLogHeaderFilter = new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				Element element2 = (Element)element;
				return element instanceof Element && 
						!DocumentUtility.LOG_HEADER.equals(element2.getNodeName()) && 
						!DocumentUtility.END_SESSION.equals(element2.getAttribute(DocumentUtility.ATTRIBUTE_TITLE)) && 
						!DocumentUtility.LOG_UNDOTG.equals(element2.getNodeName());
			}
		};
		viewer.addFilter(removeLogHeaderFilter);
		
		new DrillDownAdapter(viewer);
		
		ColumnViewerToolTipSupport.enableFor(viewer);
		
		this.parent = parent;
		Tree tree = viewer.getTree();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		tree.setLayoutData(data);
		
		makeActions();
		hookDoubleClickAction();
		contributeToActionBars();		
	}

	public void setInput(Document xmlLogDoc, Document xmlInputDoc, Map<Element, Element> mapLogInput, final Map<Element, URI> inputToUri) {
//		if(ConfigScanConfiguration.WRITE_MEMORY_TO_DISC_AS_XML) {
//		    SimpleDateFormat sdf = new SimpleDateFormat(ConfigScanConfiguration.DATE_FORMAT);
//		    Calendar c1 = Calendar.getInstance(); // today
//		    String today = sdf.format(c1.getTime());
//			String currentFilename = "XML_LOG_" + today + ".xml";
//			String xmlLog = documentUtility.parse(xmlLogDoc);
//			try {
//				BufferedWriter out = new BufferedWriter(new FileWriter(currentFilename));
//				out.write(xmlLog);
//				out.close();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//		}
		viewer.setInput(xmlLogDoc);
		
		labelProvider.setElementMap(mapLogInput);
		labelProvider.setInputEObjectMap(inputToUri);

		viewer.setLabelProvider(labelProvider);
		viewer.expandToLevel(ConfigScanConfiguration.EXPAND_LEVEL);
		viewer.refresh();
		
		int runs = documentUtility.getNumberOfRuns(xmlLogDoc);
		int successes = documentUtility.getNumberOfSuccess(xmlLogDoc);
		int failures = documentUtility.getNumberOfFailure(xmlLogDoc);
		int time = 0;
		labels.updateLabels(runs, failures, successes, time);
	}
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager toolBarManager = bars.getToolBarManager();
		FileAction file = new FileAction(parent, viewer, labels);	
		toolBarManager.add(file);
		toolBarManager.add(toggleContent);
		toolBarManager.add(collapseAll);
		toolBarManager.add(expandAll);
		toolBarManager.add(failures);
		toolBarManager.add(nextFailure);
		toolBarManager.add(previousFailure);
	}

	private void makeActions() {
		collapseAll = new Action() {
			public void run() {
				viewer.collapseAll();
				viewer.expandToLevel(ConfigScanConfiguration.EXPAND_LEVEL);
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
				return element instanceof Element && !documentUtility.hasSuccessStatus((Element)element);
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
				ISelection selection = viewer.getSelection();
				IStructuredSelection ss = ((IStructuredSelection) selection);
				Element lastSelected = ((Element) ss.getFirstElement());
				Document logDocument = ((ContentProvider) viewer.getContentProvider()).getInput();
				NodeList allElements = null;
				if(logDocument != null) {
					allElements = logDocument.getElementsByTagName(DocumentUtility.NODE_NAME_LOG_MSG);	
				}
				if(allElements != null) {
					if(lastSelected == null) {
						lastSelected = (Element) allElements.item(0);
						ss = new StructuredSelection(lastSelected);
						viewer.setSelection(ss);
					}
				} else {
					return;
				}
				Element nextEl = null;
				lastSelected = findNextDeepestChild(lastSelected);
				for(int i = 0; i < allElements.getLength() - 1; i++) {
					if(allElements.item(i).equals(lastSelected)) {
						do {
							nextEl = (Element)(allElements.item(++i)); 
						} while(i < allElements.getLength() - 1 && !DocumentUtility.ATTRIBUTE_VALUE_E.equals(
								nextEl.getAttribute(DocumentUtility.ATTRIBUTE_STATUS)));
						break;
					}
				}
				if(nextEl != null && DocumentUtility.ATTRIBUTE_VALUE_E.equals(DocumentUtility.ATTRIBUTE_STATUS)) {
					StructuredSelection next = new StructuredSelection(nextEl);
					viewer.setSelection(next);
				}
				else {
					viewer.setSelection(ss);
				}
			}
		};
		nextFailure.setText("Show next failure");
		nextFailure.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.SELECT_NEXT));
		nextFailure.setToolTipText("Jump to next failed test");

		previousFailure = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				IStructuredSelection ss = ((IStructuredSelection) selection);
				Element lastSelected = ((Element) ss.getFirstElement());
				
				Document logDocument = ((ContentProvider) viewer.getContentProvider()).getInput();
				NodeList allElements = null;
				
				if(logDocument != null) {
					allElements = logDocument.getElementsByTagName(DocumentUtility.NODE_NAME_LOG_MSG);	
				}
				
				if(allElements != null) {
					if(lastSelected == null) {
						lastSelected = (Element) allElements.item(0);
						ss = new StructuredSelection(lastSelected);
					}
				}
				else {
					return;
				}
				
				Element nextEl = null;
				
				lastSelected = findNextDeepestChild(lastSelected);
				
				for(int i = allElements.getLength() - 1; i > 0; i--) {
					if(allElements.item(i).equals(lastSelected)) {
						do {
							
							nextEl = (Element)(allElements.item(--i)); 
						} while(i > 1 && !DocumentUtility.ATTRIBUTE_VALUE_E.equals(
								nextEl.getAttribute(DocumentUtility.ATTRIBUTE_STATUS)));
						break;
					}

				}
				
				if(nextEl != null && DocumentUtility.ATTRIBUTE_VALUE_E.equals(nextEl.getAttribute(DocumentUtility.ATTRIBUTE_STATUS))) {
						StructuredSelection next = new StructuredSelection(nextEl);
						viewer.setSelection(next);
				}
				else {
					viewer.setSelection(ss);
				}
			}
		};
		previousFailure.setText("Previous failure");
		previousFailure.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.SELECT_PREV));
		previousFailure.setToolTipText("Jump to previous failed test");
		
		toggleContent = new Action("", IAction.AS_CHECK_BOX) {
			public void run() {
				Map<Element, Element> mapLogInput = ((LabelProvider) viewer.getLabelProvider()).getMapLogInput();
				Map<Element, URI> inputToEObject = ((LabelProvider) viewer.getLabelProvider()).getMapInputUri();
				if(mapLogInput != null && inputToEObject != null) {
					LabelProvider defaultLabelProvider = (LabelProvider)viewer.getLabelProvider();
					if(isChecked()) {
						setChecked(true);
						Pair<IConfigScanXMLProvider, IConfigScanLabelProvider> pair = extensionPointReader.getExtensions().get(fileExtension);
						if(pair != null) {
							IConfigScanLabelProvider second = pair.getSecond();
							second.setElementMap(mapLogInput);
							second.setInputEObjectMap(inputToEObject);
							defaultLabelProvider.setDelegate(true);
							defaultLabelProvider.setLabelProviderExtension(second);
						}
						
						toggleContent.setImageDescriptor(imageHelper.getImageDescriptor("cmlt.png"));
						toggleContent.setToolTipText("Display with ConfigScan labels");
						viewer.refresh();
					} else {
						defaultLabelProvider.setDelegate(false);
						setChecked(false);
						defaultLabelProvider.setDelegate(false);
						defaultLabelProvider.setLabelProviderExtension(null);
						labelProvider.setElementMap(mapLogInput);
						labelProvider.setInputEObjectMap(inputToEObject);
						toggleContent.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.FYSBEE));
						toggleContent.setToolTipText("Display with CMLT labels");
					}
				}
				viewer.refresh();
			}
		};
		toggleContent.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.FYSBEE));
		toggleContent.setToolTipText("Display with CMLT labels");
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
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Map<Element, Element> mapLogInput = ((LabelProvider) viewer.getLabelProvider()).getMapLogInput();
				Map<Element, URI> inputToUri = ((LabelProvider) viewer.getLabelProvider()).getMapInputUri();
				if(mapLogInput != null && inputToUri != null) {
					Element el =  (Element) selection.getFirstElement();
					Element inputEl = mapLogInput.get(el);
					if(inputEl != null) {
						URI uri = inputToUri.get(inputEl);
						
						if(uri != null) {
							Resource res = new XtextResourceSet().getResource(uri, true);
							if (res == null) {
								throw new IllegalArgumentException("resource null");
							}
							
							
							EObject eObj = res.getResourceSet().getEObject(uri, true);
							if(eObj != null) {
	
								IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
								try {
									Resource eResource = eObj.eResource();
									IFile file = ResourceUtil.getFile(eResource);
									IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new java.net.URI(uri.toString()));
									for (IFile f : files) {
										System.err.println(f.getFullPath());
									}
									
									IEditorPart openEditor = IDE.openEditor(activePage, file, true);
									if(openEditor instanceof XtextEditor) {
										INode n = NodeModelUtils.getNode(eObj);
										((XtextEditor)openEditor).selectAndReveal(n.getOffset(), n.getLength());
									}
								} catch (PartInitException e) {
									e.printStackTrace();
								} catch (URISyntaxException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		});
	}
}

