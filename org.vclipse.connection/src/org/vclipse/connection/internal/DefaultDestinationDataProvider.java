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

import java.util.Properties;

import org.vclipse.connection.IConnection;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.IDestinationDataProvider;

import com.google.inject.Inject;
import com.sap.conn.jco.ext.DestinationDataEventListener;

/**
 * @author as
 *
 */
public class DefaultDestinationDataProvider implements IDestinationDataProvider {

	/**
	 * 
	 */
	private final IConnectionHandler handler;
	
	/**
	 * @param connectionHandler
	 */
	@Inject
	public DefaultDestinationDataProvider(IConnectionHandler connectionHandler) {
		handler = connectionHandler;
	}
	
	/**
	 * @see com.sap.conn.jco.ext.DestinationDataProvider#getDestinationProperties(java.lang.String)
	 */
	@Override
	public Properties getDestinationProperties(final String name) {
		final IConnection connection = handler.getCurrentConnection();
		if(connection == null) {
			return null;
		} else {
			final Properties properties = new Properties();
			properties.setProperty(SYSTEM_NAME, connection.getSystemName());
			properties.setProperty(JCO_SYSNR, connection.getSystemNumber());
			properties.setProperty(JCO_ASHOST, connection.getHostName());
			properties.setProperty(JCO_CLIENT, connection.getClientNumber());
			properties.setProperty(JCO_USER, connection.getUserName());
			if(connection instanceof AbstractConnection) {				
				properties.setProperty(JCO_PASSWD, ((AbstractConnection)connection).getPassword());
			}
			properties.setProperty(JCO_LANG, connection.getLanguage());
			return properties;
		}
	}

	/**
	 * @see com.sap.conn.jco.ext.DestinationDataProvider#setDestinationDataEventListener(com.sap.conn.jco.ext.DestinationDataEventListener)
	 */
	@Override
	public void setDestinationDataEventListener(final DestinationDataEventListener listener) {
		// not needed
	}

	/**
	 * @see com.sap.conn.jco.ext.DestinationDataProvider#supportsEvents()
	 */
	@Override
	public boolean supportsEvents() {
		return false;
	}


}
