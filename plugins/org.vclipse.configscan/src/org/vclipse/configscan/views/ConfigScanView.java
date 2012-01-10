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
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
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
import org.vclipse.configscan.ConfigScanPlugin;
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
	
	private DrillDownAdapter drillDownAdapter;
	private Action failures;
	private Action rerunAll;
	private Action rerunFailures;
	private Action nextFailure;
	private Action previousFailure;
	private Action showTime;
	private Action stopAtFirstFailure;
	private Action jumpToFile;
	private Action collapseAll;
	private Action expandAll;
	private Action toggleContent;
	
	
	protected SuccessFilter sFilter;
	protected FailureFilter filter;

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
		viewer.addFilter(new RemoveLogHeaderFilter());
		
		drillDownAdapter = new DrillDownAdapter(viewer);
		
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

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ConfigScanView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
		
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(toggleContent);
		manager.add(collapseAll);
		manager.add(expandAll);
		manager.add(failures);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(failures);
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		FileAction file = new FileAction(parent, viewer, labels);	
		manager.add(file);
		
		manager.add(toggleContent);
		manager.add(collapseAll);
		manager.add(expandAll);
		manager.add(failures);
		
		
		manager.add(nextFailure);
		manager.add(previousFailure);
	}

	private void makeActions() {
		
		new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Element obj = ((Element)((IStructuredSelection)selection).getFirstElement());
				
			}
		};

		collapseAll = new Action() {
			public void run() {
				viewer.collapseAll();
				viewer.refresh();
				viewer.expandToLevel(ConfigScanConfiguration.EXPAND_LEVEL);
			}
		};
		collapseAll.setText("Collapse all");
		
		collapseAll.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.COLLAPSE_ALL));
		collapseAll.setToolTipText("Collapse all");
		
		expandAll = new Action() {
			public void run() {
				viewer.refresh();
				viewer.expandAll();
			}
		};
		expandAll.setText("Expand all");
		expandAll.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.EXPAND_ALL));
		expandAll.setToolTipText("Expand all");

		failures = new Action("", IAction.AS_CHECK_BOX) {
			public void run() {
				failures.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.FAILURES));
				if(filter == null) {
					setChecked(true);				
					filter = new FailureFilter();
					viewer.addFilter(filter);
				} else {
					setChecked(false);
					viewer.removeFilter(filter);
					filter = null;
				}
				viewer.refresh();
			}
		};
		failures.setToolTipText("Show only failures");
		failures.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.FAILURES));
		
		rerunAll = new Action() {
			public void run() {
				
			}
		};
		rerunAll.setText("Rerun all tests");
		rerunAll.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.RELAUNCH));
		rerunAll.setToolTipText("Reruns all tests");
		
		rerunFailures = new Action() {
			public void run() {
				
			}
		};
		rerunFailures.setText("Rerun failures");
		rerunFailures.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.RELAUNCHF));
		rerunFailures.setToolTipText("Reruns all failed tests");
		
		nextFailure = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				IStructuredSelection ss = ((IStructuredSelection) selection);
				Element lastSelected = ((Element) ss.getFirstElement());
				Document logDocument = ((ContentProvider) viewer.getContentProvider()).getInput();
				NodeList allElements = null;
				if(logDocument != null) {
					allElements = logDocument.getElementsByTagName("log_msg");	
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
						} while(i < allElements.getLength() - 1 && !"E".equals(nextEl.getAttribute("status")));
						break;
					}
				}
				if(nextEl != null && "E".equals(nextEl.getAttribute("status"))) {
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
					allElements = logDocument.getElementsByTagName("log_msg");	
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
						} while(i > 1 && !"E".equals(nextEl.getAttribute("status")));
						break;
					}

				}
				
				if(nextEl != null && "E".equals(nextEl.getAttribute("status"))) {
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
		
		showTime = new Action("Show Time") {
			public void run() {
			}
		};
		showTime.setToolTipText("Show Time");
		
		stopAtFirstFailure = new Action("Stop at first Failure") {
			public void run() {
			}
		};
		stopAtFirstFailure.setToolTipText("Stop at first Failure");
		
		
		jumpToFile = new Action("Jump into File") {
			public void run() {
			}
		};
		jumpToFile.setToolTipText("Opens the file and jumps to location");
		
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
						
						toggleContent.setImageDescriptor(ConfigScanPlugin.getDefault().getImageRegistry().getDescriptor("cmlt"));
						toggleContent.setToolTipText("Display with ConfigScan labels");
						viewer.refresh();
					} else {
						defaultLabelProvider.setDelegate(false);
						setChecked(false);
						defaultLabelProvider.setDelegate(false);
						defaultLabelProvider.setLabelProviderExtension(null);
						labelProvider.setElementMap(mapLogInput);
						labelProvider.setInputEObjectMap(inputToEObject);
						toggleContent.setImageDescriptor(ConfigScanPlugin.getDefault().getImageRegistry().getDescriptor("configscan"));
						toggleContent.setToolTipText("Display with CMLT labels");
					}
				}
				viewer.refresh();
			}
		};
		toggleContent.setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.FYSBEE));
		toggleContent.setToolTipText("Display with CMLT labels");
		
		
	}

	protected Element findNextDeepestChild(Element lastSelected) {		// TODO: testing!
		if("3".equals(lastSelected.getAttribute("level"))) {
			return lastSelected;
		}
		
		
		NodeList nodes = lastSelected.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextDeepestChild = findNextDeepestChild((Element) nodes.item(i));
				if(nextDeepestChild != null && "3".equals(nextDeepestChild.getAttribute("level"))) {
					return nextDeepestChild;
				}
			}
		}
		// TODO: if-loop below is obviously not necessary
		if("2".equals(lastSelected.getAttribute("level"))) {
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

	
}

