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
package org.vclipse.connection;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.security.storage.StorageException;
import org.osgi.service.prefs.BackingStoreException;

import com.sap.conn.idoc.IDocRepository;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoRepository;

/**
 * 
 */
public interface IConnectionHandler {

	/**
	 * @param connection
	 */
	public void addConnection(final IConnection connection);
	
	/**
	 * @param connection
	 */
	public void removeConnection(final IConnection connection);
	
	/**
	 * @param listener
	 */
	public void addConnectionListener(final IConnectionListener listener);
	
	/**
	 * @param listener
	 */
	public void removeConnectionListener(final IConnectionListener listener);

	/**
	 * @param connection
	 * @param monitor
	 * @throws JCoException 
	 */
	public IStatus connect(final IConnection connection) throws JCoException;
	
	/**
	 * 
	 */
	public void disconnect();
	
	/**
	 * @return
	 */
	public IConnection[] getAvailableConnections();
	
	/**
	 * @return
	 */
	public IConnection getCurrentConnection();
	
	/**
	 * @return
	 * @throws JCoException 
	 */
	public JCoDestination getJCoDestination() throws JCoException;
	
	/**
	 * @return
	 * @throws JCoException 
	 */
	public JCoRepository getJCoRepository() throws JCoException;

	/**
	 * @return
	 * @throws JCoException 
	 */
	public JCoFunction getJCoFunction(final String name) throws JCoException;
	
	/**
	 * @return
	 * @throws JCoException 
	 */
	public JCoFunctionTemplate getJCoFunctionTemplate(final String name) throws JCoException;
	
	/**
	 * @return
	 * @throws JCoException 
	 */
	public IDocRepository getIDocRepository() throws JCoException;

	/**
	 * @throws BackingStoreException 
	 * @throws IOException 
	 * @throws StorageException 
	 * 
	 */
	public void storeConnectionData();
}
