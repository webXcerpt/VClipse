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
package org.vclipse.connection.wizards.imports;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.connection.internal.AbstractConnection;
import org.vclipse.connection.internal.SimpleConnection;
import org.vclipse.connection.wizards.AbstractWizardPage;
import org.vclipse.connection.wizards.IInterestingINISections;

/**
 *
 */
public final class ConnectionsImportWizardPage extends AbstractWizardPage implements IInterestingINISections {
		
	/**
	 * The last path used by the file selection dialog(for the better user experience)
	 */
	private String lastPath;
	
	/**
	 * @param pageName
	 */
	protected ConnectionsImportWizardPage(final String pageName) {
		super(pageName);
		setTitle("SAP connection data import wizard.");
		setDescription("Please select a target file and SAP connections for import.");
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		targetFileLabel.setText("INI file to parse:");
	}
	
	/**
	 * @return
	 */
	public AbstractConnection[] getSelectedConnections() {
		return connections.toArray(new AbstractConnection[connections.size()]);
	}
	
	/**
	 * 
	 */
	@Override
	protected void handleBrowseForTargetFileButtonPushed() {
		final FileDialog fileDialog = new FileDialog(getShell());
		fileDialog.setFilterExtensions(new String[]{"*.ini"});
		fileDialog.setText("Please select an ini-file.");
		fileDialog.setOverwrite(false);
		
		if(lastPath != null) {
			final int lastIndex = lastPath.lastIndexOf('/');
			fileDialog.setFilterPath(lastPath.substring(0, lastIndex));
			fileDialog.setFileName(lastPath.substring(lastIndex));
		}

		final String path = fileDialog.open();
		if(path != null) {
			lastPath = path;
			targetFileText.setText(path);
		}
		validatePage();
	}

	/**
	 * @see org.vclipse.connection.wizards.AbstractWizardPage#handleTargetFileTextModified(org.eclipse.swt.events.ModifyEvent)
	 */
	@Override
	protected void handleTargetFileTextModified(final ModifyEvent event) {
		super.handleTargetFileTextModified(event);
		try {
			final HierarchicalINIConfiguration iniConfiguration = new HierarchicalINIConfiguration(new File(targetFileText.getText()));
			iniConfiguration.setDetailEvents(false);
			iniConfiguration.setThrowExceptionOnMissing(false);
			
			final Iterator<?> keys = iniConfiguration.getKeys(SYSTEM_NAME);
			final List<AbstractConnection> connections = new ArrayList<AbstractConnection>();
			while(keys.hasNext()) {
				final AbstractConnection connection = new SimpleConnection();
				final String key = (String)keys.next();
				connection.setSystemName(validateString(iniConfiguration.getString(key)));
				connection.setSystemNumber(validateString(iniConfiguration.getString(key.replaceFirst(SYSTEM_NAME, SYSTEM_NUMBER))));
				connection.setHostName(validateString(iniConfiguration.getString(key.replaceFirst(SYSTEM_NAME, HOST_NAME))));
				connection.setClientNumber(validateString(iniConfiguration.getString(key.replaceFirst(SYSTEM_NAME, CLIENT_NUMBER))));
				connection.setUserName(validateString(iniConfiguration.getString(key.replaceFirst(SYSTEM_NAME, USER_NAME))));
				connection.setPassword(validateString(iniConfiguration.getString(key.replaceFirst(SYSTEM_NAME, PASSWORD))));
				connection.setLanguage(validateString(iniConfiguration.getString(key.replaceFirst(SYSTEM_NAME, LANGUAGE))));
				connections.add(connection);
				tableViewer.add(connection);
			}
		} catch (ConfigurationException exception) {
			VClipseConnectionPlugin.log(exception.getMessage(), exception);
		}		
	}	
	
	/**
	 * @param string
	 * @return
	 */
	private String validateString(final String string) {
		return string == null || "null".equals(string) ? "" : string;
	}
}
