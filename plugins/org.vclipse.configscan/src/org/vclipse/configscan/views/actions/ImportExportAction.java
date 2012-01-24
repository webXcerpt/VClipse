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
package org.vclipse.configscan.views.actions;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xtext.util.Files;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestRunAdapter;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.utils.TestCaseUtility;
import org.vclipse.configscan.views.ConfigScanView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ImportExportAction extends SimpleTreeViewerAction implements IMenuCreator, SelectionListener {
	
	// id for the save/export item in menu
	private static final int EXPORT_ITEM_LOG = 0;
	
	// id for the import item in menu
	private static final int IMPORT_ITEM = 1;
	
	// 
	private static final int EXPORT_ITEM_INPUT = 2;
	
	private Menu menu;
	
	@Inject
	private DocumentUtility documentUtility;
	
	@Inject
	private TestCaseUtility testCaseUtility;
	
	public ImportExportAction(ConfigScanView view, ConfigScanImageHelper imageHelper, 
			DocumentUtility documentUtility, TestCaseUtility testCaseUtility) {
		super(view, imageHelper);
		setMenuCreator(this);
		setText("File");
		setToolTipText("File menu");	
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.DISK));	
		this.documentUtility = documentUtility;
		this.testCaseUtility = testCaseUtility;
	}
	
	@Override
	public void dispose() {
		if(menu != null) {
			menu.dispose();
			menu = null;
		}
	}

	@Override
	public Menu getMenu(Control parent) {
		if(menu != null) {
			menu.dispose();
		}
		menu = new Menu(parent);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Save ConfigScan log file");
		item.setID(EXPORT_ITEM_LOG);
		item.addSelectionListener(this);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Save ConfigScan input file");
		item.setID(EXPORT_ITEM_INPUT);
		item.addSelectionListener(this);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setID(IMPORT_ITEM);
		item.setText("Import ConfigScan log file");
		item.addSelectionListener(this);
		return menu;	
	}

	@Override
	public Menu getMenu(Menu parent) {
		return null;
	}
	
	@Override
	public void widgetSelected(SelectionEvent event) {
		Object source = event.getSource();
		if(source instanceof MenuItem) {
			MenuItem menuItem = (MenuItem)source;
			int id = menuItem.getID();
			Shell activeShell = Display.getDefault().getActiveShell();
			if(IMPORT_ITEM == id) {
				List<TestCase> testCases = Lists.newArrayList();
				FileDialog fileDialog = new FileDialog(activeShell, SWT.OPEN);
				fileDialog.setText("File dialog for log file import");
				String selectedPath = fileDialog.open();
				if(selectedPath != null) {
					String content = Files.readFileIntoString(selectedPath);
					Document document = documentUtility.parse(content);
					Node nextSibling = document.getDocumentElement().getFirstChild().getNextSibling();
					NodeList childNodes = nextSibling.getChildNodes();
					for(int i=0; i<childNodes.getLength(); i++) {
						Node item = childNodes.item(i);
						if(item.getNodeType() == Node.ELEMENT_NODE) {
							Element element = (Element)item;
							TestCase testCase = 
										testCaseUtility.createTestCase(element, null, documentUtility, new HashMap<String, Object>());
							if(testCase != null) {
								testCases.add(testCase);								
							}
						}
					}	
					treeViewer.setInput(testCases);
				}
			} else if(EXPORT_ITEM_LOG == id) {
				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				if(firstElement instanceof TestRunAdapter) {
					TestRunAdapter testRun = (TestRunAdapter)firstElement;
					Document logDocument = testRun.getLogDocument();
					if(logDocument == null) {
						ConfigScanPlugin.log("Log document not available for " + testRun.getLabel(null), IStatus.ERROR);
					} else {
						FileDialog fileDialog = new FileDialog(activeShell, SWT.SAVE);
						fileDialog.setText("File dialog for log file export");
						String path = fileDialog.open();
						if(path != null) {
							Files.writeStringIntoFile(path, documentUtility.parse(logDocument));
						}
					}
				}
			} else if(EXPORT_ITEM_INPUT == id) {
				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				if(firstElement instanceof TestRunAdapter) {
					TestRunAdapter testRun = (TestRunAdapter)firstElement;
					Document logDocument = testRun.getInputDocument();
					if(logDocument == null) {
						ConfigScanPlugin.log("Input document not available for " + testRun.getLabel(null), IStatus.ERROR);
					} else {
						FileDialog fileDialog = new FileDialog(activeShell, SWT.SAVE);
						fileDialog.setText("File dialog for input file export");
						String path = fileDialog.open();
						if(path != null) {
							Files.writeStringIntoFile(path, documentUtility.parse(logDocument));
						}
					}
				}
			}
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		// not used
	};
}
