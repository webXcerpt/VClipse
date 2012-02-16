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
import org.vclipse.base.ui.ClasspathAwareImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.utils.TestCaseFactory;
import org.vclipse.configscan.views.ConfigScanView;
import org.vclipse.configscan.views.ConfigScanViewInput;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;

public final class ImportExportAction extends SimpleTreeViewerAction implements IMenuCreator, SelectionListener {
	
	public static final String ID = ConfigScanPlugin.ID + "." + ImportExportAction.class.getSimpleName();
	
	// id for the save/export item in menu
	private static final int EXPORT_ITEM_LOG = 0;
	
	// id for the import item in menu
	private static final int IMPORT_ITEM = 1;
	
	// 
	private static final int EXPORT_ITEM_INPUT = 2;
	
	private Menu menu;
	

	private DocumentUtility documentUtility;
	
	private TestCaseFactory testCaseUtility;
	
	public ImportExportAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper, DocumentUtility documentUtility, TestCaseFactory testCaseFactory) {
		super(view, imageHelper);
		setMenuCreator(this);
		setText("File");
		setToolTipText("File menu");	
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.DISK));	
		this.documentUtility = documentUtility;
		this.testCaseUtility = testCaseFactory;
		setId(ID);
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
				FileDialog fileDialog = new FileDialog(activeShell, SWT.OPEN);
				fileDialog.setText("File dialog for log file import");
				String selectedPath = fileDialog.open();
				if(selectedPath != null) {
					String content = Files.readFileIntoString(selectedPath);
					Document document = documentUtility.parse(content);
					TestRun testRun = testCaseUtility.buildTestRun("Imported Config Scan input file", null, (IConfigScanXMLProvider)null, null);
					testRun.setLogElement(document);
					ConfigScanViewInput input = new ConfigScanViewInput();
					input.setTestRuns(Lists.newArrayList(testRun));
					input.setDate(null, IConfigScanConfiguration.DATE_FORMAT_UI_ENTRIES);
					input.setConfigurationName(testRun.getTitle());
					testRun.addTestCase(testCaseUtility.buildTestCase(document, testRun));	
					view.setInput(input);
				}
			} else {
				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
				if(selection.isEmpty()) {
					setSelectionToFirstElement();
				}
				if(!selection.isEmpty()) {
					TestRun testRun = (TestRun)selection.getFirstElement();
					FileDialog fileDialog = new FileDialog(activeShell, SWT.SAVE);
					if(EXPORT_ITEM_LOG == id) {
						Document logDocument = (Document)testRun.getLogElement();
						if(logDocument == null) {
							ConfigScanPlugin.log("Log document not available for " + testRun.getLabel(null), IStatus.ERROR);
						} else {
							fileDialog.setText("File dialog for log file export");
							String path = fileDialog.open();
							if(path != null) {
								Files.writeStringIntoFile(path, documentUtility.serialize(logDocument));
							}
						}
					} else if(EXPORT_ITEM_INPUT == id) {
						Document inputDocument = (Document)testRun.getInputElement();
						if(inputDocument == null) {
							ConfigScanPlugin.log("Input document not available for " + testRun.getLabel(null), IStatus.ERROR);
						} else {
							fileDialog.setText("File dialog for input file export");
							String path = fileDialog.open();
							if(path != null) {
								Files.writeStringIntoFile(path, documentUtility.serialize(inputDocument));
							}
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
