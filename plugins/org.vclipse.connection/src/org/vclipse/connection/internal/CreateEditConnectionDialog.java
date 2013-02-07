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
package org.vclipse.connection.internal;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 *
 */
public class CreateEditConnectionDialog extends TitleAreaDialog {
	
	/**
	 * 
	 */
	private AbstractConnection connection;
	
	/**
	 * 
	 */
	private Text systemNameText;
	private Text systemNumberText;
	private Text hostNameText;
	private Text clientNumberText;
	private Text userNameText;
	private Text passwordText;
	private Text languageText;
	
	/**
	 * 
	 */
	private String systemName;
	private String systemNumber;
	private String hostName;
	private String clientNumber;
	private String userName;
	private String password;
	private String language;
	
	/**
	 * @param parentShell
	 */
	public CreateEditConnectionDialog(final Shell parentShell, final AbstractConnection current) {
		super(parentShell);
		connection = current == null ? new SimpleConnection() : current;
	}

	/**
	 * @return
	 */
	public AbstractConnection getNewSAPConnection() {
		return connection;
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		getShell().setText("Connection Dialog");
		setTitle("Connection Dialog");
		setMessage("This dialog allows you to modify the properties of a SAP connection.");
		((GridLayout)parent.getLayout()).marginTop = 20;
		
		final Composite mainArea = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		mainArea.setLayoutData(gridData);
		mainArea.setLayout(new GridLayout(2, false));
		
		Label label = new Label(mainArea, SWT.NONE);
		label.setText("System name:");
		
		systemNameText = new Text(mainArea, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		systemNameText.setLayoutData(gridData);
		systemNameText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent event) {
				systemName =  ((Text)event.widget).getText();
			}
		});
		
		label = new Label(mainArea, SWT.NONE);
		label.setText("Application server host name:");
		
		hostNameText = new Text(mainArea, SWT.BORDER);
		hostNameText.setLayoutData(gridData);
		hostNameText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent event) {
				hostName = ((Text)event.widget).getText();
			}
		});
		
		label = new Label(mainArea, SWT.NONE);
		label.setText("System number:");
		
		systemNumberText = new Text(mainArea, SWT.BORDER);
		systemNumberText.setLayoutData(gridData);
		systemNumberText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent event) {
				systemNumber = ((Text)event.widget).getText();
			}
		});
				
		label = new Label(mainArea, SWT.NONE);
		label.setText("Client number:");
		
		clientNumberText = new Text(mainArea, SWT.BORDER);
		clientNumberText.setLayoutData(gridData);
		clientNumberText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent event) {
				clientNumber = ((Text)event.widget).getText();
			}
		});
				
		label = new Label(mainArea, SWT.NONE);
		label.setText("User name:");
		
		userNameText = new Text(mainArea, SWT.BORDER);
		userNameText.setLayoutData(gridData);
		userNameText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent event) {
				userName = ((Text)event.widget).getText();
			}
		});
			
		label = new Label(mainArea, SWT.NONE);
		label.setText("Password:");
		
		passwordText = new Text(mainArea, SWT.BORDER);
		passwordText.setLayoutData(gridData);
		passwordText.setEchoChar('*');
		passwordText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent event) {
				password = ((Text)event.widget).getText();
			}
		});
		
		label = new Label(mainArea, SWT.NONE);
		label.setText("Language:");
		
		languageText = new Text(mainArea, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		languageText.setLayoutData(gridData);
		languageText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent event) {
				language = ((Text)event.widget).getText();
			}
		});
		
		final Button button = new Button(mainArea, SWT.CHECK | SWT.LEFT);
		button.setText("Show password");
		gridData = new GridData();
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				if(((Button)event.widget).getSelection()) {
					passwordText.setEchoChar('\0');
				} else {
					passwordText.setEchoChar('*');
				}
			}
		});
		systemNameText.setText(connection.getSystemName());
		systemNumberText.setText(connection.getSystemNumber());
		hostNameText.setText(connection.getHostName());
		clientNumberText.setText(connection.getClientNumber());
		userNameText.setText(connection.getUserName());
		passwordText.setText(connection.getPassword());
		languageText.setText(connection.getLanguage());
		return parent;
	}

	/**
	 * @see org.eclipse.jface.dialogs.MessageDialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(final int buttonId) {
		if(buttonId == 0) {
			connection.setSystemName(systemName);
			connection.setSystemNumber(systemNumber);
			connection.setHostName(hostName);
			connection.setClientNumber(clientNumber);
			connection.setUserName(userName);
			connection.setPassword(password);
			connection.setLanguage(language);
		}
		super.buttonPressed(buttonId);
	}	
}
