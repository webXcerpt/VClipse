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
package org.vclipse.connection.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.vclipse.connection.IConnection;
import org.vclipse.connection.IConnectionDataStorage;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.IConnectionListener;
import org.vclipse.connection.IDestinationDataProvider;
import org.vclipse.connection.VClipseConnectionPlugin;

import com.google.inject.Inject;
import com.sap.conn.idoc.IDocRepository;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.ext.Environment;

/**
 *
 */
public final class ConnectionHandler implements IConnectionHandler {
	
	/**
	 *	Registry for connection listener
	 */
	private Set<IConnectionListener> connectionListener;
	
	/**
	 * 
	 */
	private List<IConnection> availableConnections;
	
	/**
	 * 
	 */
	private JCoDestination jCoDestination;
	
	/**
	 * 
	 */
	private int currentConnectionIndex = DefaultConnectionDataStorage.BAD_INDEX;
	
	/**
	 * 
	 */
	private final IDestinationDataProvider destinationDataProvider;
	
	/**
	 * 
	 */
	private final IConnectionDataStorage storage;
	
	/**
	 * @return
	 */
	@Inject
	public ConnectionHandler(IConnectionDataStorage storage, IDestinationDataProvider destinationDataProvider) {
		this.destinationDataProvider = destinationDataProvider;
		this.storage = storage;
		Environment.registerDestinationDataProvider(destinationDataProvider);
	}
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		if(Environment.isDestinationDataProviderRegistered()) {
			Environment.unregisterDestinationDataProvider(destinationDataProvider);
		}
		connectionListener.clear();
		availableConnections.clear();
		super.finalize();
	}
	
	/**
	 * @see org.vclipse.connection.IConnectionHandler#addConnectionListener(org.vclipse.connection.IConnectionListener)
	 */
	@Override
	public void addConnectionListener(final IConnectionListener listener) {
		if(connectionListener == null) {
			connectionListener = new HashSet<IConnectionListener>();
		}
		if(listener != null) {
			connectionListener.add(listener);
		}
	}
	
	/**
	 * @see org.vclipse.connection.IConnectionHandler#removeConnectionListener(org.vclipse.connection.IConnectionListener)
	 */
	@Override
	public void removeConnectionListener(final IConnectionListener listener) {
		if(connectionListener != null) {			
			connectionListener.remove(listener);
		}
	}

	/**
	 * @see org.vclipse.connection.IConnectionHandler#addConnection(org.vclipse.connection.SapConnection)
	 */
	@Override
	public void addConnection(final IConnection connection) {
		if(availableConnections == null) {
			loadConnectionData();
		}
		if(connection != null && !availableConnections.contains(connection)) {
			availableConnections.add(connection);
		}
	}
	
	/**
	 * @see org.vclipse.connection.IConnectionHandler#removeConnection(org.vclipse.connection.SapConnection)
	 */
	@Override
	public void removeConnection(final IConnection connection) {
		if(availableConnections != null) {
			if(hasActiveConnection() && availableConnections.get(currentConnectionIndex).equals(connection)) {
				disconnect();
			}
			if(availableConnections.indexOf(connection) < currentConnectionIndex) {
				currentConnectionIndex--;
			}
			availableConnections.remove(connection);
		}
	}
	
	/**
	 * @throws JCoException 
	 * @see org.vclipse.connection.IConnectionHandler#connect(org.vclipse.connection.SapConnection, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus connect(final IConnection connection) throws JCoException {
		if(hasActiveConnection()) {
			disconnect();
		}
		final IStatus status = connection.readyToConnect();
		if(IStatus.OK == status.getSeverity()) {
			if(availableConnections == null) {
				loadConnectionData();
			}
			currentConnectionIndex = availableConnections.indexOf(connection);
			if(connectionListener != null) {				
				for(final IConnectionListener listener : connectionListener) {
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							listener.connected(connection);
						}
					});
				}
			}
			return new Status(IStatus.OK, VClipseConnectionPlugin.ID, "Connected to '" + connection.getSystemName() + "'");		
		} else {
			return status;
		}
	}
	
	/**
	 * @see org.vclipse.connection.IConnectionHandler#disconnect()
	 */
	@Override
	public void disconnect() {
		if(connectionListener != null && hasActiveConnection()) {
			for(IConnectionListener listener : connectionListener) {
				listener.disconnected();
			}
		}
		jCoDestination = null;
		currentConnectionIndex = DefaultConnectionDataStorage.BAD_INDEX;
	}
	
	/**
	 * @see org.vclipse.connection.IConnectionHandler#getAvailableConnections()
	 */
	public IConnection[] getAvailableConnections() {
		if(availableConnections == null) {
			loadConnectionData();
		}			
		return availableConnections.toArray(new AbstractConnection[availableConnections.size()]);
	}
	
	/**
	 * @see org.vclipse.connection.IConnectionHandler#getCurrentConnection()
	 */
	public IConnection getCurrentConnection() {
		if(hasActiveConnection()) {			
			return availableConnections.get(currentConnectionIndex);
		} else {
			return null;
		}
	}	
	
	/**
	 * @throws JCoException 
	 * 
	 */
	@Override
	public JCoDestination getJCoDestination() throws JCoException {
		if(jCoDestination == null) {
			jCoDestination = JCoDestinationManager.getDestination("VClipse");
			final JCoFunction function = getJCoFunction("STFC_CONNECTION");
			function.execute(jCoDestination);
		}
		return jCoDestination;
	}
	
	/**
	 * 
	 */
	@Override
	public IDocRepository getIDocRepository() throws JCoException {
		final JCoDestination destination = getJCoDestination();
		return destination == null ? null : JCoIDoc.getIDocRepository(destination);
	}
	
	/**
	 */
	@Override
	public JCoRepository getJCoRepository() throws JCoException {
		final JCoDestination destination = getJCoDestination();
		return destination == null ? null : destination.getRepository();
	}
	
	/**
	 * 
	 */
	@Override
	public JCoFunction getJCoFunction(final String name) throws JCoException {
		final JCoFunctionTemplate template = getJCoFunctionTemplate(name);
		return template == null ? null : template.getFunction();
	}

	/**
	 * 
	 */
	@Override
	public JCoFunctionTemplate getJCoFunctionTemplate(final String name) throws JCoException {
		final JCoRepository repository = getJCoRepository();
		return repository == null ? null : repository.getFunctionTemplate(name);
	}
	
	/**
	 * @return
	 */
	private boolean hasActiveConnection() {
		if(availableConnections == null) {
			loadConnectionData();
		}
		if(availableConnections.isEmpty()) {
			return false;
		} else {
			return currentConnectionIndex > DefaultConnectionDataStorage.BAD_INDEX;
		}
	}

	/**
	 * @see org.vclipse.connection.IConnectionHandler#storeConnectionData()
	 */
	@Override
	public void storeConnectionData() {
		if(availableConnections != null) {			
			storage.storeConnectionData(availableConnections, currentConnectionIndex);
		}
	}

	/**
	 * 
	 */
	private void loadConnectionData() {
		availableConnections = storage.loadConnectionData(false);
		currentConnectionIndex = storage.getCurrentConnectionIndex();
	}
}
