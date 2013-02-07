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
package org.vclipse.connection.dialogs;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vclipse.connection.IConnection;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.connection.internal.AbstractConnection;
import org.vclipse.connection.internal.CreateEditConnectionDialog;

import com.google.inject.Inject;

/**
 *	
 */
public class PreferencePage extends org.eclipse.jface.preference.PreferencePage implements IWorkbenchPreferencePage {
	
	/**
	 * 
	 */
	public static final String ACTUAL_CONNECTION__BACKGROUND = VClipseConnectionPlugin.ID + ".actualConnectionBackground";
	
	
	/**
	 * 
	 */
	private TableViewer tableViewer;
	
	/**
	 * 
	 */
	private Button editButton;
	
	/**
	 * 
	 */
	private Button connectButton;
	
	/**
	 * 
	 */
	private Button disconnectButton;
	
	/**
	 * 
	 */
	private Button deleteAllButton;

	/**
	 * 
	 */
	private Button deleteButton;
	
	/**
	 * 
	 */
	private final IConnectionHandler handler;
	
	/**
	 * 
	 */
	@Inject
	public PreferencePage(IConnectionHandler connectionHandler) {
		handler = connectionHandler;
	}
	
	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(final IWorkbench workbench) {
		setMessage("SAP Connection");
		setDescription("Description");
	}
	
	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	@Override
	protected void performApply() {
		handler.storeConnectionData();
		super.performApply();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		handler.storeConnectionData();
		return super.performOk();
	}
	
	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(final Composite parent) {
		final Composite mainArea = new Composite(parent, SWT.NONE);
		mainArea.setLayout(new GridLayout(2, false));
		//GridData gridData = new GridData(GridData.FILL_BOTH);
		//mainArea.setLayoutData(gridData);
		
		tableViewer = new TableViewer(mainArea, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 300;
		gridData.verticalSpan = 10;
		final Table table = tableViewer.getTable();
		table.setLayoutData(gridData);
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider(handler));
		tableViewer.setSorter(new TableHeaderSorter());
		createColumns(null);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				handleTableSelection();
			}
		});

		final IConnection[] connections = handler.getAvailableConnections();
		if(connections.length > 0) {
			tableViewer.setInput(connections);
		}
		
		gridData = new GridData();
		gridData.widthHint = 85;
		connectButton = new Button(mainArea, SWT.PUSH);
		connectButton.setText("Connect");
		connectButton.setLayoutData(gridData);
		connectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				handleConnectButtonPushed();
			}
		});
		
		disconnectButton = new Button(mainArea, SWT.PUSH);
		disconnectButton.setText("Disconnect");
		disconnectButton.setLayoutData(gridData);
		disconnectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				handleDisconnectButtonPushed();
			}
		});
		
		new Composite(mainArea, SWT.NONE);
		
		final Button button = new Button(mainArea, SWT.PUSH);
		button.setText("Create");
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				handleCreateButtonPushed();
			}
		});
		
		editButton = new Button(mainArea, SWT.PUSH);
		editButton.setText("Edit");
		editButton.setLayoutData(gridData);
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				handleEditButtonPushed();
			}
		});
		
		deleteButton = new Button(mainArea, SWT.PUSH);
		deleteButton.setText("Delete");
		deleteButton.setLayoutData(gridData);
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				handleDeleteButtonPushed();
			}
		});
		
		deleteAllButton = new Button(mainArea, SWT.PUSH);
		deleteAllButton.setText("Delete all");
		deleteAllButton.setLayoutData(gridData);
		deleteAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				handleDeleteAllButtonPushed();
			}
		});
		handleTableSelection();
		return parent;
	}
	
	/**
	 *	Handles the disconnect operation
	 */
	private void handleDisconnectButtonPushed() {
		handler.disconnect();
		tableViewer.refresh(handler.getCurrentConnection());
		handleTableSelection();
	}
	
	/**
	 *	Handles the connect operation
	 */
	private void handleConnectButtonPushed() {
		final IConnection oldConnection = handler.getCurrentConnection();
		final IConnection connection = (IConnection)((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
		try {
			IStatus status = new ConnectionStateDialog(getShell(), handler).connect(connection);
			if(status != null && IStatus.ERROR == status.getSeverity()) {
				new MessageDialog(getShell(), "Connection state", null, status.getMessage(), 
					MessageDialog.ERROR, new String[]{"OK"}, 0).open();
			} else {
				new MessageDialog(getShell(), "Connection state", null, "Connected to '" + connection.getSystemName() + "'", 
					MessageDialog.INFORMATION, new String[]{"OK"}, 0).open();
				tableViewer.refresh(connection, true);
				if(oldConnection != null) {
					tableViewer.refresh(oldConnection, true);
				}
				handler.storeConnectionData();
			}
			handleTableSelection();
		} catch (Throwable exception) {
			final String errorMessage = "Connection to '" + connection.getSystemName() + 
			"' was not successful!\n" + "\n\nReason:\n\t" + exception.getMessage();
			new MessageDialog(getShell(), "Connection state", null, errorMessage, MessageDialog.ERROR, 
					new String[]{"OK"}, 0).open();
		}		
	}
	
	/**
	 *	Handles the create operation
	 */
	private void handleCreateButtonPushed() {
		final CreateEditConnectionDialog dialog = new CreateEditConnectionDialog(getShell(), null);
		if(Window.OK == dialog.open()) {
			final AbstractConnection newConnection = dialog.getNewSAPConnection();
			handler.addConnection(newConnection);
			handler.storeConnectionData();
			
			// do not use tableViewer.add(...) ==> there are problems with the header sorting, 
			// since tableViewer.refresh() is used for updating the tableViewer
			tableViewer.setInput(handler.getAvailableConnections());
		}
		
	}
	
	/**
	 *	Handles the delete operation
	 */
	private void handleDeleteButtonPushed() {
		final IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
		if(!selection.isEmpty()) {
			final AbstractConnection connection = (AbstractConnection)selection.getFirstElement();
			if(connection.equals(handler.getCurrentConnection())) {
				handler.disconnect();
			}
			handler.removeConnection(connection);
			tableViewer.setInput(handler.getAvailableConnections());
			handleTableSelection();
			handler.storeConnectionData();
		}
	}
	
	/**
	 *	Handles the edit operation
	 */
	private void handleEditButtonPushed() {
		final AbstractConnection connection = (AbstractConnection)((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
		if(Window.OK == new CreateEditConnectionDialog(getShell(), connection).open()) {
			tableViewer.refresh(connection);
			handler.storeConnectionData();
		}
	}
	
	/**
	 *	Handles the delete operation for all connections
	 */
	private void handleDeleteAllButtonPushed() {
		for(IConnection connection : handler.getAvailableConnections()) {
			handler.removeConnection(connection);
		}
		tableViewer.setInput(handler.getAvailableConnections());
		handleTableSelection();
		handler.storeConnectionData();		
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void handleTableSelection() {
		final Iterator<AbstractConnection> iterator = ((IStructuredSelection)tableViewer.getSelection()).iterator();
		int selectionCounter = 0;
		connectButton.setEnabled(false);
		disconnectButton.setEnabled(false);
		editButton.setEnabled(false);
		deleteButton.setEnabled(false);
		deleteAllButton.setEnabled(false);
		while(iterator.hasNext()) {
			if(selectionCounter == 1) {
				final boolean activate = iterator.next().equals(handler.getCurrentConnection());
				connectButton.setEnabled(!activate);
				disconnectButton.setEnabled(activate);
				editButton.setEnabled(!activate);
			}
			selectionCounter++;
			if(selectionCounter > 2) {
				break;
			}
		}
		if(selectionCounter > 0) {
			deleteButton.setEnabled(true);
			deleteAllButton.setEnabled(true);
		}
	}
	
	/**
	 *	Creates table columns
	 * @param sortListener 
	 */
	private void createColumns(Listener sortListener) {
		final Table table = tableViewer.getTable();
		
		TableColumn column = new TableColumn(table, SWT.CENTER);
		column.setWidth(30);
		
		TableHeaderSelectionListener listener = new TableHeaderSelectionListener(tableViewer, handler);
		column = new TableColumn(table, SWT.LEFT);
		column.setText("System name");
		column.setWidth(120);
		table.setSortColumn(column);
		column.addSelectionListener(listener);
		
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Host name");
		column.setWidth(120);
		column.addSelectionListener(listener);
		
		column = new TableColumn(table, SWT.LEFT);
		column.setText("User name");
		column.setWidth(100);
		column.addSelectionListener(listener);
		
		column = new TableColumn(table, SWT.LEFT);
		column.setText("System number");
		column.setWidth(120);
		column.addSelectionListener(listener);
		
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Client number");
		column.setWidth(120);
		column.addSelectionListener(listener);
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setSortDirection(SWT.UP);
	}
}
