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
package org.vclipse.connection.wizards.exports;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.internal.AbstractConnection;
import org.vclipse.connection.wizards.AbstractWizardPage;

import com.google.inject.Inject;

/**
 *
 */
public class ConnectionsExportWizardPage extends AbstractWizardPage {

	/**
	 * 
	 */
	private boolean exportPasswords;
	
	/**
	 * 
	 */
	private boolean overwriteExistingFile;
	
	/**
	 * 
	 */
	private final IConnectionHandler handler;
	
	/**
	 * @param pageName
	 */
	@Inject
	protected ConnectionsExportWizardPage(final String pageName, IConnectionHandler connectionHandler) {
		super(pageName);
		handler = connectionHandler;
		setTitle("SAP connection data export wizard");
		setDescription("Please select a target file and SAP connections for export");
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(final Composite parent) {		
		super.createControl(parent);
		
		targetFileLabel.setText("Export to file:");
		
		final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		
		Button button = new Button(connectionsArea, SWT.CHECK);
		button.setText("Overwrite existing file");
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				overwriteExistingFile = ((Button)event.widget).getSelection();
			}		
		});
		
		button = new Button(connectionsArea, SWT.CHECK);
		button.setText("Export passwords");
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				exportPasswords = ((Button)event.widget).getSelection();
			}
		});
		tableViewer.setInput(handler.getAvailableConnections());
	}
	
	/**
	 * @return
	 */
	public AbstractConnection[] getConnections2Export() {
		return connections.toArray(new AbstractConnection[connections.size()]);
	}
	
	/**
	 * @return
	 */
	public File getTargetFile() {
		return targetFile;
	}

	/**
	 * @return
	 */
	public boolean exportPassword() {
		return exportPasswords;
	}
	
	/**
	 * @return
	 */
	public boolean overwriteExistingFile() {
		return overwriteExistingFile;
	}
}
