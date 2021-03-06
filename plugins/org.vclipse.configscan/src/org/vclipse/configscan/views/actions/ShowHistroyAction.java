/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan.views.actions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ConfigScanView;
import org.vclipse.configscan.views.ConfigScanViewInput;
import org.vclipse.configscan.views.TestRunsHistory;

public final class ShowHistroyAction extends SimpleTreeViewerAction implements IMenuCreator, SelectionListener {

	public static final String ID = ConfigScanPlugin.ID + "." + ShowHistroyAction.class.getSimpleName();
	
	private static final int IMPORT_HISTORY = 1001;
	private static final int EXPORT_HISTORY = 1002;
	private static final int CLEAR_HISTORY = 1003;
	
	private Menu menu;
	
	private TestRunsHistory history;

	private ConfigScanView view;
	
	public ShowHistroyAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper, TestRunsHistory history) {
		super(view, imageHelper);
		setMenuCreator(this);
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.HISTORY));
		setText("Test run history");
		setToolTipText("Test run history");
		this.history = history;
		this.view = view;
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
		
		List<ConfigScanViewInput> historyEntries = history.getHistory();
		for(int i=historyEntries.size()-1; i>-1; i--) {
			ConfigScanViewInput input = historyEntries.get(i);
			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(input.getConfigurationName() + " on " + input.getDate());
			item.setID(historyEntries.indexOf(input));
			item.setImage(imageHelper.getImage(IConfigScanImages.TEST));
			item.addSelectionListener(this);
		}
		
		MenuItem item = new MenuItem(menu, SWT.SEPARATOR);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Import ...");
		item.addSelectionListener(this);
		item.setID(IMPORT_HISTORY);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Export ...");
		item.addSelectionListener(this);
		item.setID(EXPORT_HISTORY);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Clear ...");
		item.setID(CLEAR_HISTORY);
		item.addSelectionListener(this);
		
		return menu;
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		MenuItem item = (MenuItem)event.getSource();
		int id = item.getID();
		if(CLEAR_HISTORY == id) {
			history.clear();
			view.setInput(null);
		} else if(IMPORT_HISTORY == id) {
			// TODO what should happen if the history is full ?
			FileDialog fileDialog = new FileDialog(view.getSite().getShell(), SWT.OPEN);
			fileDialog.setFilterExtensions(new String[]{"*.xml"});
			String selectedpath = fileDialog.open();
			if(selectedpath != null) {
				try {
					history.load(selectedpath);
				} catch (FileNotFoundException exception) {
					ConfigScanPlugin.log("Could not import history from a file." + exception.getMessage(), IStatus.ERROR);
				}
			}
		} else if(EXPORT_HISTORY == id) {
			FileDialog fileDialog = new FileDialog(view.getSite().getShell(), SWT.SAVE);
			fileDialog.setFilterExtensions(new String[]{"*.xml"});
			String selectedpath = fileDialog.open();
			if(selectedpath != null) {
				try {
					history.save(selectedpath);
				} catch (IOException exception) {
					ConfigScanPlugin.log("Could not export history to a file." + exception.getMessage(), IStatus.ERROR);
				}
			}
		} else {
			view.setInput(history.getHistory().get(id));
		}
	}

	@Override
	public Menu getMenu(Menu parent) {
		// not used
		return null;
	}
	
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// not used
	}
}
