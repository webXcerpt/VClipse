/** 
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.connection.wizards.imports

import java.io.File
import java.util.ArrayList
import java.util.Iterator
import java.util.List
import org.apache.commons.configuration.ConfigurationException
import org.apache.commons.configuration.HierarchicalINIConfiguration
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.FileDialog
import org.vclipse.connection.VClipseConnectionPlugin
import org.vclipse.connection.internal.AbstractConnection
import org.vclipse.connection.internal.SimpleConnection
import org.vclipse.connection.wizards.AbstractWizardPage
import org.vclipse.connection.wizards.IInterestingINISections

/** 
 */
final class ConnectionsImportWizardPage extends AbstractWizardPage implements IInterestingINISections {
	/** 
	 * The last path used by the file selection dialog(for the better user experience)
	 */
	String lastPath

	/** 
	 * @param pageName
	 */
	protected new(String pageName) {
		super(pageName)
		setTitle("SAP connection data import wizard.")
		setDescription("Please select a target file and SAP connections for import.")
	}

	/** 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	override void createControl(Composite parent) {
		super.createControl(parent)
		targetFileLabel.setText("INI file to parse:")
	}

	/** 
	 * @return
	 */
	def AbstractConnection[] getSelectedConnections() {
		return connections.toArray(newArrayOfSize(connections.size()))
	}

	/** 
	 */
	override protected void handleBrowseForTargetFileButtonPushed() {
		val FileDialog fileDialog = new FileDialog(getShell())
		fileDialog.setFilterExtensions((#["*.ini"] as String[]))
		fileDialog.setText("Please select an ini-file.")
		fileDialog.setOverwrite(false)
		if (lastPath !== null) {
			val int lastIndex = lastPath.lastIndexOf(Character.valueOf('/').charValue)
			fileDialog.setFilterPath(lastPath.substring(0, lastIndex))
			fileDialog.setFileName(lastPath.substring(lastIndex))
		}
		val String path = fileDialog.open()
		if (path !== null) {
			lastPath = path
			targetFileText.setText(path)
		}
		validatePage()
	}

	/** 
	 * @see org.vclipse.connection.wizards.AbstractWizardPage#handleTargetFileTextModified(org.eclipse.swt.events.ModifyEvent)
	 */
	override protected void handleTargetFileTextModified(ModifyEvent event) {
		super.handleTargetFileTextModified(event)
		try {
			val HierarchicalINIConfiguration iniConfiguration = new HierarchicalINIConfiguration(
				new File(targetFileText.getText()))
			iniConfiguration.setDetailEvents(false)
			iniConfiguration.setThrowExceptionOnMissing(false)
			val Iterator<?> keys = iniConfiguration.getKeys(SYSTEM_NAME)
			val List<AbstractConnection> connections = new ArrayList<AbstractConnection>()
			while (keys.hasNext()) {
				val AbstractConnection connection = new SimpleConnection()
				val String key = keys.next() as String
				connection.setSystemName(validateString(iniConfiguration.getString(key)))
				connection.setSystemNumber(
					validateString(iniConfiguration.getString(key.replaceFirst(SYSTEM_NAME, SYSTEM_NUMBER))))
				connection.setHostName(
					validateString(iniConfiguration.getString(key.replaceFirst(SYSTEM_NAME, HOST_NAME))))
				connection.setClientNumber(
					validateString(iniConfiguration.getString(key.replaceFirst(SYSTEM_NAME, CLIENT_NUMBER))))
				connection.setRouter(
					validateString(iniConfiguration.getString(key.replaceFirst(SYSTEM_NAME, ROUTER))))
				connection.setUserName(
					validateString(iniConfiguration.getString(key.replaceFirst(SYSTEM_NAME, USER_NAME))))
				connection.setPassword(
					validateString(iniConfiguration.getString(key.replaceFirst(SYSTEM_NAME, PASSWORD))))
				connection.setLanguage(
					validateString(iniConfiguration.getString(key.replaceFirst(SYSTEM_NAME, LANGUAGE))))
				connections.add(connection)
				tableViewer.add(connection)
			}

		} catch (ConfigurationException exception) {
			VClipseConnectionPlugin::log(exception.getMessage(), exception)
		}

	}

	/** 
	 * @param string
	 * @return
	 */
	def private String validateString(String string) {
		return if(string === null || "null".equals(string)) "" else string
	}

}
