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
package org.vclipse.connection.wizards.exports

import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.ArrayList
import java.util.List
import org.apache.commons.configuration.ConfigurationException
import org.apache.commons.configuration.HierarchicalINIConfiguration
import org.apache.commons.configuration.tree.DefaultConfigurationNode
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
import org.eclipse.jface.dialogs.ErrorDialog
import org.eclipse.jface.operation.IRunnableWithProgress
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.jface.wizard.IWizardContainer
import org.eclipse.jface.wizard.Wizard
import org.eclipse.ui.IExportWizard
import org.eclipse.ui.IWorkbench
import org.vclipse.connection.IConnectionHandler
import org.vclipse.connection.VClipseConnectionPlugin
import org.vclipse.connection.internal.AbstractConnection
import org.vclipse.connection.wizards.IInterestingINISections
import com.google.inject.Inject

/** 
 */
class ConnectionsExportWizard extends Wizard implements IExportWizard, IInterestingINISections {
	/** 
	 */
	static final String PROPERTY_NAME = "sap_connection_"
	/** 
	 */
	ConnectionsExportWizardPage page
	/** 
	 */
	final IConnectionHandler handler

	/** 
	 * @param connectionHandler
	 */
	@Inject new(IConnectionHandler connectionHandler) {
		handler = connectionHandler
	}

	/** 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	override void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Connection Data Export Wizard")
	}

	/** 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	override void addPages() {
		addPage(page = new ConnectionsExportWizardPage("", handler))
	}

	/** 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	override boolean performFinish() {
		val IRunnableWithProgress runnable = ([ IProgressMonitor monitor | // Create target file
			val File targetFile = page.getTargetFile()
			if (targetFile.exists() && !page.overwriteExistingFile()) {
				throw new InvocationTargetException(
					new CoreException(
						new Status(IStatus::ERROR, VClipseConnectionPlugin::ID, "Can not overwrite existing file!")))
			} else {
				try {
					targetFile.createNewFile()
				} catch (IOException exception) {
					throw new InvocationTargetException(exception)
				}
				val AbstractConnection[] connections = page.getConnections2Export()
				val int totalWork = if(page.exportPassword()) connections.length * 7 else connections.length * 6
				monitor.beginTask('''Exporting SAP systems to the file «targetFile.getName()»'''.toString, totalWork)
				val HierarchicalINIConfiguration iniConfiguration = new HierarchicalINIConfiguration()
				// SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MMMM.yyyy");
				// Calendar calendar = Calendar.getInstance();
				// System.out.println(dateFormatter.format(calendar.getTime()));
				var List<DefaultConfigurationNode> nodes = new ArrayList<DefaultConfigurationNode>(connections.length)

				for (var int index = 0; index < connections.length; index++) {
					nodes.add(new DefaultConfigurationNode(PROPERTY_NAME + index, {
						val _rdIndx_connections = index
						connections.get(_rdIndx_connections)
					}.getSystemName()))
				}
				iniConfiguration.addNodes(SYSTEM_NAME, nodes)
				monitor.worked(connections.length)
				nodes.clear()
				for (var int index = 0; index < connections.length; index++) {
					nodes.add(new DefaultConfigurationNode(PROPERTY_NAME + index, {
						val _rdIndx_connections = index
						connections.get(_rdIndx_connections)
					}.getHostName()))
				}
				iniConfiguration.addNodes(HOST_NAME, nodes)
				monitor.worked(connections.length)
				nodes.clear()
				for (var int index = 0; index < connections.length; index++) {
					nodes.add(new DefaultConfigurationNode(PROPERTY_NAME + index, {
						val _rdIndx_connections = index
						connections.get(_rdIndx_connections)
					}.getSystemNumber()))
				}
				iniConfiguration.addNodes(SYSTEM_NUMBER, nodes)
				monitor.worked(connections.length)
				nodes.clear()
				for (var int index = 0; index < connections.length; index++) {
					nodes.add(new DefaultConfigurationNode(PROPERTY_NAME + index, {
						val _rdIndx_connections = index
						connections.get(_rdIndx_connections)
					}.getClientNumber()))
				}
				iniConfiguration.addNodes(CLIENT_NUMBER, nodes)
				monitor.worked(connections.length)
				nodes.clear()
				for (var int index = 0; index < connections.length; index++) {
					nodes.add(new DefaultConfigurationNode(PROPERTY_NAME + index, {
						val _rdIndx_connections = index
						connections.get(_rdIndx_connections)
					}.getUserName()))
				}
				iniConfiguration.addNodes(USER_NAME, nodes)
				monitor.worked(connections.length)
				/*if(page.exportPassword()) {
				 * 			nodes.clear();
				 * 			for(int index=0; index<connections.length; index++) {
				 * 				nodes.add(new DefaultConfigurationNode(PROPERTY_NAME + index, connections[index].get()));
				 * 			}
				 * 			iniConfiguration.addNodes(PASSWORD, nodes);
				 * 			monitor.worked(connections.length);
				 }*/
				nodes.clear()
				for (var int index = 0; index < connections.length; index++) {
					nodes.add(new DefaultConfigurationNode(PROPERTY_NAME + index, {
						val _rdIndx_connections = index
						connections.get(_rdIndx_connections)
					}.getLanguage()))
				}
				iniConfiguration.addNodes(LANGUAGE, nodes)
				monitor.worked(connections.length)
				try {
					iniConfiguration.save(targetFile)
				} catch (ConfigurationException exception) {
					throw new InvocationTargetException(exception)
				}
				monitor.done()
			}
		] as IRunnableWithProgress)
		val IWizardContainer wcontainer = getContainer()
		try {
			wcontainer.run(false, true, runnable)
		} catch (InvocationTargetException e) {
			ErrorDialog::openError(wcontainer.getShell(), "Error Dialog", "Could not export SAP systems to an INI-file",
				(e.getTargetException() as CoreException).getStatus())
			return false
		} catch (InterruptedException e) {
			ErrorDialog::openError(wcontainer.getShell(), "Error Dialog", "Could not export SAP systems to an INI-file",
				new Status(IStatus::ERROR, VClipseConnectionPlugin::ID, e.getMessage()))
			return false
		}
		return true
	}

}
