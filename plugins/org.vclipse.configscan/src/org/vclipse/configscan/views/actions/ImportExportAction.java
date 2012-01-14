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

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.xtext.util.Files;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.utils.TestCaseUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ImportExportAction extends Action implements IMenuCreator, SelectionListener {
	
	// id for the save/export item in menu
	private static final int EXPORT_ITEM = 0;
	
	// id for the import item in menu
	private static final int IMPORT_ITEM = 1;
	
	private Menu menu;
	
	private TreeViewer viewer;
	
	private ConfigScanImageHelper imageHelper;

	private DocumentUtility documentUtility;
	
	private TestCaseUtility testCaseUtility;
	
	public ImportExportAction() {
		setMenuCreator(this);
		setText("File");
		setToolTipText("File menu");	
	}
	
	@Inject
	public void setImageHelper(ConfigScanImageHelper imageHelper) {
		this.imageHelper = imageHelper;
		if(this.imageHelper != null) {
			setImageDescriptor(
					this.imageHelper.getImageDescriptor(IConfigScanImages.DISK));			
		}
	}
	
	@Inject
	public void setDocumentUtility(DocumentUtility documentUtility) {
		this.documentUtility = documentUtility;
	}
	
	@Inject
	public void setTestCaseUtility(TestCaseUtility testCaseUtility) {
		this.testCaseUtility = testCaseUtility;
	}
	
	public void setTreeViewer(TreeViewer treeViewer) {
		this.viewer = treeViewer;
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
		MenuItem item = new MenuItem(menu, SWT.PUSH | SWT.Deactivate);
		item.setText("Save ConfigScan log file");
		item.setID(EXPORT_ITEM);
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
	
//			FileDialog fd = new FileDialog(parent.getShell(), SWT.SAVE);
//			fd.setText("XML File Dialog");
//			System.err.println("Trying to show FileDialog");
//			String path = fd.open();
//			if(path == null) {
//				// Cancelled
//			}
//			else {
//				String content = new XmlLoader().parseXmlToString(((ContentProvider) viewer.getContentProvider()).getLogDocument());
//				Files.writeStringIntoFile(path, content);
//			}

	@Override
	public void widgetSelected(SelectionEvent event) {
		Object source = event.getSource();
		if(source instanceof MenuItem) {
			MenuItem menuItem = (MenuItem)source;
			int id = menuItem.getID();
			if(IMPORT_ITEM == id) {
				List<TestCase> testCases = Lists.newArrayList();
				FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
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
							if(documentUtility.passesFilter(element)) {
								TestCase testCase = testCaseUtility.createTestCase(element, null);
								testCases.add(testCase);
							}
						}
					}	
					viewer.setInput(testCases);
				}
			}
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		// not used
	};
}
