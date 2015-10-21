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
package org.vclipse.connection.internal

import java.util.HashSet
import java.util.List
import java.util.Set
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
import org.eclipse.swt.widgets.Display
import org.vclipse.connection.IConnection
import org.vclipse.connection.IConnectionDataStorage
import org.vclipse.connection.IConnectionHandler
import org.vclipse.connection.IConnectionListener
import org.vclipse.connection.IDestinationDataProvider
import org.vclipse.connection.VClipseConnectionPlugin
import com.google.inject.Inject
import com.sap.conn.idoc.IDocRepository
import com.sap.conn.idoc.jco.JCoIDoc
import com.sap.conn.jco.JCoDestination
import com.sap.conn.jco.JCoDestinationManager
import com.sap.conn.jco.JCoException
import com.sap.conn.jco.JCoFunction
import com.sap.conn.jco.JCoFunctionTemplate
import com.sap.conn.jco.JCoRepository
import com.sap.conn.jco.ext.Environment

/** 
 */
final class ConnectionHandler implements IConnectionHandler {
	/** 
	 * Registry for connection listener
	 */
	Set<IConnectionListener> connectionListener
	/** 
	 */
	List<IConnection> availableConnections
	/** 
	 */
	JCoDestination jCoDestination
	/** 
	 */
	int currentConnectionIndex = DefaultConnectionDataStorage::BAD_INDEX
	/** 
	 */
	final IDestinationDataProvider destinationDataProvider
	/** 
	 */
	final IConnectionDataStorage storage

	/** 
	 * @return
	 */
	@Inject new(IConnectionDataStorage storage, IDestinationDataProvider destinationDataProvider) {
		this.destinationDataProvider = destinationDataProvider
		this.storage = storage
		Environment::registerDestinationDataProvider(destinationDataProvider)
	}

	/** 
	 * @see java.lang.Object#finalize()
	 */
	override protected void finalize() throws Throwable {
		if (Environment::isDestinationDataProviderRegistered()) {
			Environment::unregisterDestinationDataProvider(destinationDataProvider)
		}
		connectionListener.clear()
		availableConnections.clear()
		super.finalize()
	}

	/** 
	 * @see org.vclipse.connection.IConnectionHandler#addConnectionListener(org.vclipse.connection.IConnectionListener)
	 */
	override void addConnectionListener(IConnectionListener listener) {
		if (connectionListener === null) {
			connectionListener = new HashSet<IConnectionListener>()
		}
		if (listener !== null) {
			connectionListener.add(listener)
		}

	}

	/** 
	 * @see org.vclipse.connection.IConnectionHandler#removeConnectionListener(org.vclipse.connection.IConnectionListener)
	 */
	override void removeConnectionListener(IConnectionListener listener) {
		if (connectionListener !== null) {
			connectionListener.remove(listener)
		}

	}

	/** 
	 * @see org.vclipse.connection.IConnectionHandler#addConnection(org.vclipse.connection.SapConnection)
	 */
	override void addConnection(IConnection connection) {
		if (availableConnections === null) {
			loadConnectionData()
		}
		if (connection !== null && !availableConnections.contains(connection)) {
			availableConnections.add(connection)
		}

	}

	/** 
	 * @see org.vclipse.connection.IConnectionHandler#removeConnection(org.vclipse.connection.SapConnection)
	 */
	override void removeConnection(IConnection connection) {
		if (availableConnections !== null) {
			if (hasActiveConnection() && availableConnections.get(currentConnectionIndex).equals(connection)) {
				disconnect()
			}
			if (availableConnections.indexOf(connection) < currentConnectionIndex) {
				currentConnectionIndex--
			}
			availableConnections.remove(connection)
		}

	}

	/** 
	 * @throws JCoException 
	 * @see org.vclipse.connection.IConnectionHandler#connect(org.vclipse.connection.SapConnection, org.eclipse.core.runtime.IProgressMonitor)
	 */
	override IStatus connect(IConnection connection) throws JCoException {
		if (hasActiveConnection()) {
			disconnect()
		}
		val IStatus status = connection.readyToConnect()
		if (IStatus::OK === status.getSeverity()) {
			if (availableConnections === null) {
				loadConnectionData()
			}
			currentConnectionIndex = availableConnections.indexOf(connection)
			if (connectionListener !== null) {
				for (IConnectionListener listener : connectionListener) {
					Display::getDefault().syncExec(([|listener.connected(connection)] as Runnable))
				}

			}
			return new Status(IStatus::OK, VClipseConnectionPlugin::ID,
				'''Connected to '«»«connection.getSystemName()»'«»'''.toString)
		} else {
			return status
		}
	}

	/** 
	 * @see org.vclipse.connection.IConnectionHandler#disconnect()
	 */
	override void disconnect() {
		if (connectionListener !== null && hasActiveConnection()) {
			for (IConnectionListener listener : connectionListener) {
				listener.disconnected()
			}

		}
		jCoDestination = null
		currentConnectionIndex = DefaultConnectionDataStorage::BAD_INDEX
	}

	/** 
	 * @see org.vclipse.connection.IConnectionHandler#getAvailableConnections()
	 */
	override IConnection[] getAvailableConnections() {
		if (availableConnections === null) {
			loadConnectionData()
		}
		return availableConnections.toArray(newArrayOfSize(availableConnections.size()))
	}

	/** 
	 * @see org.vclipse.connection.IConnectionHandler#getCurrentConnection()
	 */
	override IConnection getCurrentConnection() {
		if (hasActiveConnection()) {
			return availableConnections.get(currentConnectionIndex)
		} else {
			return null
		}
	}

	/** 
	 * @throws JCoException 
	 */
	override JCoDestination getJCoDestination() throws JCoException {
		if (jCoDestination === null) {
			jCoDestination = JCoDestinationManager::getDestination("VClipse")
			val JCoFunction function = getJCoFunction("STFC_CONNECTION")
			function.execute(jCoDestination)
		}
		return jCoDestination
	}

	/** 
	 */
	override IDocRepository getIDocRepository() throws JCoException {
		val JCoDestination destination = getJCoDestination()
		return if(destination === null) null else JCoIDoc::getIDocRepository(destination)
	}

	/** 
	 */
	override JCoRepository getJCoRepository() throws JCoException {
		val JCoDestination destination = getJCoDestination()
		return if(destination === null) null else destination.getRepository()
	}

	/** 
	 */
	override JCoFunction getJCoFunction(String name) throws JCoException {
		val JCoFunctionTemplate template = getJCoFunctionTemplate(name)
		return if(template === null) null else template.getFunction()
	}

	/** 
	 */
	override JCoFunctionTemplate getJCoFunctionTemplate(String name) throws JCoException {
		val JCoRepository repository = getJCoRepository()
		return if(repository === null) null else repository.getFunctionTemplate(name)
	}

	/** 
	 * @return
	 */
	def private boolean hasActiveConnection() {
		if (availableConnections === null) {
			loadConnectionData()
		}
		if (availableConnections.isEmpty()) {
			return false
		} else {
			return currentConnectionIndex > DefaultConnectionDataStorage::BAD_INDEX
		}
	}

	/** 
	 * @see org.vclipse.connection.IConnectionHandler#storeConnectionData()
	 */
	override void storeConnectionData() {
		if (availableConnections !== null) {
			storage.storeConnectionData(availableConnections, currentConnectionIndex)
		}

	}

	/** 
	 */
	def private void loadConnectionData() {
		availableConnections = storage.loadConnectionData(false)
		currentConnectionIndex = storage.getCurrentConnectionIndex()
	}

}
