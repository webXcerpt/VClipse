/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
/***  ****//**
 * 
 */
package org.vclipse.connection.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.connection.dialogs.CreateEditConnectionDialog;
import org.vclipse.connection.internal.AbstractConnection;

/**
 *
 */
public abstract class AbstractWizardPage extends WizardPage implements IWizardPage {

	/**
	 * 
	 */
	protected Label targetFileLabel;
	
	/**
	 * 
	 */
	protected Composite fileArea;
	
	/**
	 * 
	 */
	protected Text targetFileText;
	
	/**
	 * 
	 */
	protected File targetFile;
	
	/**
	 * 
	 */
	protected Composite connectionsArea;
	
	/**
	 * 
	 */
	protected final List<AbstractConnection> connections;
	
	/**
	 * 
	 */
	protected TableViewer tableViewer;
	
	/**
	 * @param pageName
	 */
	protected AbstractWizardPage(final String pageName) {
		this(pageName, null, null);
	}
	
	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected AbstractWizardPage(final String pageName, final String title, final ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		connections = new ArrayList<AbstractConnection>();
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(final Composite parent) {
		final Composite mainArea = new Composite(parent, SWT.NONE);
		mainArea.setLayout(new GridLayout());
		
		fileArea = new Composite(mainArea, SWT.NONE);
		fileArea.setLayout(new GridLayout(3, false));
		fileArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		targetFileLabel = new Label(fileArea, SWT.NONE);
		targetFileLabel.setText("");
		
		targetFileText = new Text(fileArea, SWT.BORDER | SWT.READ_ONLY);
		targetFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		targetFileText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				handleTargetFileTextModified(event);
			}
		});
		
		Button button = new Button(fileArea, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				handleBrowseForTargetFileButtonPushed();
			}
		});
	
		connectionsArea = new Composite(mainArea, SWT.NONE);
		connectionsArea.setLayout(new GridLayout(2, false));
		connectionsArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Label label = new Label(connectionsArea, SWT.NONE);
		label.setText("Available connections:");
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		label.setLayoutData(gridData);
		
		tableViewer = new TableViewer(connectionsArea, SWT.V_SCROLL | SWT.H_SCROLL | SWT.CHECK | 
				SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		final Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				handleTableSelection(event);
			}
		});
		
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(final DoubleClickEvent event) {
				handleTableDoubleClicked(event);
			}
		});
		
		TableColumn item = new TableColumn(table, SWT.NONE);
		item.setText("");
		item.setWidth(20);
		
		item = new TableColumn(table, SWT.LEFT);
		item.setText("System name");
		item.setWidth(150);
		
		item = new TableColumn(table, SWT.LEFT);
		item.setText("Server name");
		item.setWidth(150);
		
		item = new TableColumn(table, SWT.LEFT);
		item.setText("System number");
		item.setWidth(150);
		
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
		gridData = new GridData();
		gridData.verticalSpan = 6;
		gridData.heightHint = 200;
		table.setLayoutData(gridData);
		
		gridData = new GridData(GridData.FILL_HORIZONTAL); 
		gridData.widthHint = 100;
		
		button = new Button(connectionsArea, SWT.PUSH);
		button.setText("Create");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				handleCreateButtonPushed();
			}
		});
		button.setLayoutData(gridData);
		disableButton(button);
		
		button = new Button(connectionsArea, SWT.PUSH);
		button.setText("Check all");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				handleCheckAllButtonPushed();
			}
		});
		button.setLayoutData(gridData);
		disableButton(button);
		
		button = new Button(connectionsArea, SWT.PUSH);
		button.setText("Uncheck all");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				handleUncheckAllButtonPushed();
			}
		});
		button.setLayoutData(gridData);
		disableButton(button);
		
		button = new Button(connectionsArea, SWT.PUSH);
		button.setText("Remove");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				handleRemoveButtonPushed();
			}
		});
		button.setLayoutData(gridData);
		disableButton(button);
		
		setControl(mainArea);
		validatePage();
	}
	
	/**
	 * @param event
	 */
	protected void handleTargetFileTextModified(final ModifyEvent event) {
		targetFile = new File(((Text)event.widget).getText());
	}
	
	/**
	 * 
	 */
	protected void handleBrowseForTargetFileButtonPushed() {
		final FileDialog dialog = new FileDialog(fileArea.getShell(), SWT.OPEN);
		dialog.setFilterExtensions(new String[]{"*.ini"});
		final String absolutePath = dialog.open();
		if(absolutePath == null || absolutePath.isEmpty()) {
			targetFile = null;
		} else {
			targetFileText.setText(absolutePath);
		}
		validatePage();
	}

	/**
	 * 
	 */
	protected void handleCreateButtonPushed() {
		final CreateEditConnectionDialog dialog = new CreateEditConnectionDialog(getShell(), null);
		if(Window.OK == dialog.open()) {
			tableViewer.add(dialog.getNewSAPConnection());
		}
	}
	
	/**
	 * 
	 */
	protected void handleCheckAllButtonPushed() {
		for(TableItem item : tableViewer.getTable().getItems()) {
			item.setChecked(true);
			connections.add((AbstractConnection)item.getData());
		}
		validatePage();
	}
	
	/**
	 * 
	 */
	protected void handleUncheckAllButtonPushed() {
		for(TableItem item : tableViewer.getTable().getItems()) {
			item.setChecked(false);
			connections.remove((AbstractConnection)item.getData());
		}
		validatePage();
	}
	
	/**
	 *
	 */
	protected void handleRemoveButtonPushed() {
		final Table table = tableViewer.getTable();
		for(TableItem item : table.getItems()) {
			if(item.getChecked()) {
				final Object object = item.getData();
				tableViewer.remove(object);
				connections.remove(object);
			}
		}
		validatePage();
	}
	
	/**
	 * @param event
	 */
	protected void handleTableSelection(final SelectionEvent event) {
		if(event.detail == SWT.CHECK) {
			final TableItem item = (TableItem)event.item;
			final AbstractConnection connection = (AbstractConnection)item.getData();
			if(item.getChecked()) {
				connections.add(connection);
			} else {
				connections.remove(connection);
			}
			validatePage();
		}
	}
	
	/**
	 * @param event
	 */
	protected void handleTableDoubleClicked(final DoubleClickEvent event) {
		final AbstractConnection selected = (AbstractConnection)((IStructuredSelection)event.getSelection()).getFirstElement();
		if(Dialog.OK == new CreateEditConnectionDialog(getShell(), selected).open()) {
			tableViewer.refresh(selected);
		}
	}
	
	/**
	 * 
	 */
	protected void validatePage() {
		setPageComplete(false);
		setErrorMessage(null);
		if(targetFileText.getText().isEmpty()) {
			setErrorMessage("Please select a target file!");
		} else if(connections.isEmpty()) {
			setMessage("Please select at least one connection to finish the wizard!", WARNING);
		} else {
			setMessage("Wizard can be finished now. Please push the 'Finish'-button.", INFORMATION);
			setPageComplete(true);
		}
	}

	/**
	 * @param button
	 */
	private void disableButton(final Button button) {
		if(!VClipseConnectionPlugin.getDefault().isJCoAvailable()) {
			button.setEnabled(false);
			button.setToolTipText("Disabled because of absence of jco library");
		}
	}
}
