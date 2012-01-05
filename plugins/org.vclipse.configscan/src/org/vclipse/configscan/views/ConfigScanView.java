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
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.vclipse.configscan.ConfigScanPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.inject.Inject;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class ConfigScanView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.vclipse.configscan.ConfigScanView";

	@Inject
	private XmlLoader xmlLoader;
	
	
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action failures;
	private Action doubleClickAction;
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
	protected FailureFilter fFilter;



	private Composite parent;

	private Labels labels;


	

	

	
	
	/**
	 * The constructor.
	 */
	public ConfigScanView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		
		GridLayout gridLayout = new GridLayout(4, true);
		parent.setLayout(gridLayout);
		
		labels = new Labels(parent, 0, 0, 0, 0);
		
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ViewContentProvider());
		ColumnViewerToolTipSupport.enableFor(viewer);
		viewer.setLabelProvider(new ViewLabelProvider());		// at initialization we have no map
		viewer.addFilter(new RemoveLogHeaderFilter());
		
		
		// viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		this.parent = parent;
		Tree tree = viewer.getTree();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		tree.setLayoutData(data);
		

		// Create the help context id for the viewer's control
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "XmlTreeViewer.viewer");
		
		makeActions();
//		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		viewer.expandToLevel(Config.EXPAND_LEVEL);
		
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
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Element obj = ((Element)((IStructuredSelection)selection).getFirstElement());
				showMessage("Double-click detected on "+obj.getTagName());
			}
		};
		
		
		collapseAll = new Action("Collapse all", ConfigScanPlugin.getDefault().getImageRegistry().getDescriptor("collapseAll")) {
			public void run() {
				viewer.refresh();
				viewer.collapseAll();
				viewer.expandToLevel(Config.EXPAND_LEVEL);
			}
		};
		collapseAll.setToolTipText("Collapse all");
		
		expandAll = new Action("Expand all", ConfigScanPlugin.getDefault().getImageRegistry().getDescriptor("expandAll")) {
			public void run() {
				
				
				viewer.refresh();
				viewer.expandAll();
			}
		};
		expandAll.setToolTipText("Expand all");
		

		
		failures = new Action("", IAction.AS_CHECK_BOX) {
			public void run() {
				failures.setImageDescriptor(ConfigScanPlugin.getDefault().getImageRegistry().getDescriptor("failures"));
				
				if(fFilter == null) {
					
					setChecked(true);				
					fFilter = new FailureFilter();
					viewer.addFilter(fFilter);
				}
				
				else {
					setChecked(false);
					viewer.removeFilter(fFilter);
					fFilter = null;
				}
				
				
				viewer.refresh();
			}
		};
		failures.setToolTipText("Failures");
		failures.setImageDescriptor(ConfigScanPlugin.getDefault().getImageRegistry().getDescriptor("failures"));
		
		rerunAll = new Action("Rerun all", ConfigScanPlugin.getDefault().getImageRegistry().getDescriptor("relaunch")) {
			public void run() {
			}
		};
		rerunAll.setToolTipText("Reruns all tests");
		
		rerunFailures = new Action("Rerun failures", ConfigScanPlugin.getDefault().getImageRegistry().getDescriptor("relaunchf")) {
			public void run() {
			}
		};
		rerunFailures.setToolTipText("Reruns all failed tests");
		
		nextFailure = new Action("Next failure", ConfigScanPlugin.getDefault().getImageRegistry().getDescriptor("select_next")) {
			public void run() {
				ISelection selection = viewer.getSelection();
				IStructuredSelection ss = ((IStructuredSelection) selection);
				Element lastSelected = ((Element) ss.getFirstElement());
				
				
				Document logDocument = ((ViewContentProvider) viewer.getContentProvider()).getLogDocument();
				
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
				}
				else {
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
		nextFailure.setToolTipText("Jump to next failed test");

		
		previousFailure = new Action("Previous failure", ConfigScanPlugin.getDefault().getImageRegistry().getDescriptor("select_prev")) {
			public void run() {
				ISelection selection = viewer.getSelection();
				IStructuredSelection ss = ((IStructuredSelection) selection);
				Element lastSelected = ((Element) ss.getFirstElement());
				
				Document logDocument = ((ViewContentProvider) viewer.getContentProvider()).getLogDocument();
				
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
				
				Map<Element, Element> mapLogInput = ((ViewLabelProvider) viewer.getLabelProvider()).getMapLogInput();
				Map<Element, URI> inputToEObject = ((ViewLabelProvider) viewer.getLabelProvider()).getMapInputUri();
				
				if(mapLogInput != null && inputToEObject != null) {
					if(isChecked()) {
						
						setChecked(true);				
						viewer.setLabelProvider(new ViewOtherLabelProvider(mapLogInput, inputToEObject));
						toggleContent.setImageDescriptor(ConfigScanPlugin.getDefault().getImageRegistry().getDescriptor("cmlt"));
						toggleContent.setToolTipText("Display with ConfigScan labels");
					}
					
					else {
						setChecked(false);
						viewer.setLabelProvider(new ViewLabelProvider(mapLogInput, inputToEObject));
						toggleContent.setImageDescriptor(ConfigScanPlugin.getDefault().getImageRegistry().getDescriptor("configscan"));
						toggleContent.setToolTipText("Display with CMLT labels");
					}
				}
				
				viewer.refresh();
				
			}
		};
		toggleContent.setImageDescriptor(ConfigScanPlugin.getDefault().getImageRegistry().getDescriptor("configscan"));
		toggleContent.setToolTipText("Display with CMLT labels");
		
		
	}

	/** This method is used to find the next deepest child of an element.
	 *  
	 * @param lastSelected
	 * @return the next deepest child
	 */
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
				
				Map<Element, Element> mapLogInput = ((ViewLabelProvider) viewer.getLabelProvider()).getMapLogInput();
				Map<Element, URI> inputToUri = ((ViewLabelProvider) viewer.getLabelProvider()).getMapInputUri();
				
				
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
									XtextEditor cmltEditor = (XtextEditor)activePage.openEditor(
											new FileEditorInput(file), "com.webxcerpt.cm.nsn.cmlt.CmlT");
									INode n = NodeModelUtils.getNode(eObj);
									cmltEditor.selectAndReveal(n.getOffset(), n.getLength());
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
	
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Sample View",
			message);
	}
 
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	
	
	public void setInput(Document xmlLogDoc, Document xmlInputDoc, Map<Element, Element> mapLogInput, final Map<Element, URI> inputToUri) {
		if(Config.WRITE_MEMORY_TO_DISC_AS_XML) {
		    SimpleDateFormat sdf = new SimpleDateFormat(Config.DATE_FORMAT);
		    Calendar c1 = Calendar.getInstance(); // today
		    String today = sdf.format(c1.getTime());
			String currentFilename = "XML_LOG_" + today + ".xml";
			String xmlLog = xmlLoader.parseXmlToString(xmlLogDoc);
		    Util.saveStringToDisc(currentFilename, xmlLog);
		}
		
		ViewContentProvider contentProvider = new ViewContentProvider(xmlLogDoc);
		
		viewer.setContentProvider(contentProvider);
		
		viewer.setLabelProvider(new ViewLabelProvider(mapLogInput, inputToUri));		// this time we have a map
		
		viewer.refresh();
		viewer.expandToLevel(Config.EXPAND_LEVEL);
		
		
		int runs = contentProvider.getNumberOfRuns();
		int successes = contentProvider.getNumberOfSuccess();
		int failures = contentProvider.getNumberOfFailure();
		int time = 0;
		

		labels.updateLabels(runs, failures, successes, time);
	}

	/** For testing purposes only.
	 * 
	 * @return the TreeViewer
	 */
	public TreeViewer getTreeViewer() {
		return viewer;
	}
}

