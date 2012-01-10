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


import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.xtext.util.Files;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.utils.DocumentUtility;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.inject.Inject;


/** This class is for the File-Menu drop-down.
 * 
 * @author kulig
 *
 */
public class FileAction extends Action implements IMenuCreator {
	private Menu menu;
	private Composite parent;
	private TreeViewer viewer;
	private Labels labels;
	
	public FileAction(Composite parent, TreeViewer viewer, Labels labels) {
		this.parent = parent;
		this.viewer = viewer;
		this.labels = labels;
		setMenuCreator(this);
		setText("File");
		setToolTipText("File menu");	
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
		MenuItem saveAs = new MenuItem(menu, SWT.PUSH | SWT.Deactivate);
		saveAs.setText("Save ConfigScan log (XML) as...");
		saveAs.addSelectionListener(new SaveAsSelectionListener());
		
		MenuItem importXml = new MenuItem(menu, SWT.PUSH);
		importXml.setText("Import ConfigScan log (XML)");
		importXml.addSelectionListener(new ImportXmlSelectionListener());
		return menu;
		
	}

	@Override
	public Menu getMenu(Menu parent) {
		return null;
	}

	@Inject
	private DocumentBuilder documentBuilder;
	
	@Inject
	private DocumentUtility documentUtility;
	
	public class ImportXmlSelectionListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog fd = new FileDialog(parent.getShell(), SWT.OPEN);
			fd.setText("XML File Dialog");
			System.err.println("Trying to show FileDialog");
			String path = fd.open();
			if(path == null) {
				// Cancelled
			}
			else {
				String content = Files.readFileIntoString(path);
				try {
					Document document = documentBuilder.parse(content);
					viewer.setInput(document);
					viewer.expandToLevel(ConfigScanConfiguration.EXPAND_LEVEL);
					viewer.refresh();
					int time = 0;
					labels.updateLabels(documentUtility.getNumberOfRuns(document), documentUtility.getNumberOfFailure(document), 
							documentUtility.getNumberOfSuccess(document), time);
				} catch (SAXException exception) {
					ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR, exception);
				} catch (IOException exception) {
					ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR, exception);
				}
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}

	};
	
	public class SaveSelectionListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}

	};
	
	public class SaveAsSelectionListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
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
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}

	};
	
	
}
