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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.connection.IConnection;
import org.vclipse.connection.IConnectionDataStorage;
import org.vclipse.connection.IDestinationDataProvider;
import org.vclipse.connection.VClipseConnectionPlugin;

import com.google.inject.Inject;

/**
 *
 */
public class DefaultConnectionDataStorage implements IConnectionDataStorage {

	/**
	 * 
	 */
	public static final int BAD_INDEX = -1;
	
	/**
	 * 
	 */
	private static final String CURRENT_CONNECTION_INDEX = VClipseConnectionPlugin.ID + ".currentConnectionIndex";
	
	/**
	 * 
	 */
	private static final String NUMBER_OF_CONNECTIONS = VClipseConnectionPlugin.ID + ".numberOfConnections";
	
	/**
	 * 
	 */
	private final IPreferenceStore preferenceStore;
	
	/**
	 * 
	 */
	private final ISecurePreferences securePreferences;
	
	/**
	 * 
	 */
	private int currentConnectionIndex;
	
	/**
	 * @param preferenceStore
	 * @param securePreferences
	 */
	@Inject
	public DefaultConnectionDataStorage(IPreferenceStore preferenceStore, ISecurePreferences securePreferences) {
		this.preferenceStore = preferenceStore;
		this.securePreferences = securePreferences;
		currentConnectionIndex = BAD_INDEX;
	}
	
	/**
	 * @return
	 */
	public List<IConnection> loadConnectionData(boolean loadPassword) {
		List<IConnection> availableConnections = new ArrayList<IConnection>();
		currentConnectionIndex = preferenceStore.getInt(CURRENT_CONNECTION_INDEX);
		for(int index=0; index<preferenceStore.getInt(NUMBER_OF_CONNECTIONS); index++) {
			IConnection newConnection = null;
			if(loadPassword) {
				newConnection = new SimpleConnection();
			} else {
				newConnection = new IndexedConnection(index, securePreferences);
				try {
					newConnection.setPassword(securePreferences.get(IDestinationDataProvider.JCO_PASSWD, ""));
				} catch(final StorageException exception) {
					VClipseConnectionPlugin.log(exception.getMessage(), exception);
				}
			}
			newConnection.setSystemName(preferenceStore.getString(IDestinationDataProvider.SYSTEM_NAME + index));
			newConnection.setSystemNumber(preferenceStore.getString(IDestinationDataProvider.JCO_SYSNR + index));
			newConnection.setHostName(preferenceStore.getString(IDestinationDataProvider.JCO_ASHOST + index));
			newConnection.setClientNumber(preferenceStore.getString(IDestinationDataProvider.JCO_CLIENT + index));
			newConnection.setUserName(preferenceStore.getString(IDestinationDataProvider.JCO_USER + index));
			newConnection.setLanguage(preferenceStore.getString(IDestinationDataProvider.JCO_LANG + index));
			availableConnections.add(newConnection);
		}
		return availableConnections;
	}
	
	/**
	 * @param connections
	 */
	public void storeConnectionData(List<IConnection> connections, int currentConnectionIndex) {
		preferenceStore.setValue(CURRENT_CONNECTION_INDEX, currentConnectionIndex);
		preferenceStore.setValue(NUMBER_OF_CONNECTIONS, connections.size());
		for(int i=0; i < connections.size(); i++) {
			final IConnection current = connections.get(i);
			preferenceStore.setValue(IDestinationDataProvider.JCO_ASHOST + i, current.getHostName());
			preferenceStore.setValue(IDestinationDataProvider.JCO_SYSNR + i, current.getSystemNumber());			
			preferenceStore.setValue(IDestinationDataProvider.SYSTEM_NAME + i, current.getSystemName());
			preferenceStore.setValue(IDestinationDataProvider.JCO_CLIENT + i, current.getClientNumber());
			preferenceStore.setValue(IDestinationDataProvider.JCO_USER + i, current.getUserName());
			preferenceStore.setValue(IDestinationDataProvider.JCO_LANG + i, current.getLanguage());
			try {
				if(current instanceof AbstractConnection) {					
					securePreferences.put(IDestinationDataProvider.JCO_PASSWD + i, ((AbstractConnection)current).getPassword(), true);
				}
			} catch (final StorageException exception) {
				VClipseConnectionPlugin.log(exception.getMessage(), exception);
			}
		}
		try {
			((IPersistentPreferenceStore)preferenceStore).save();
		} catch (final IOException exception) {
			VClipseConnectionPlugin.log(exception.getMessage(), exception);
		}
		try {
			securePreferences.flush();
		}  catch (final IOException exception) {
			VClipseConnectionPlugin.log(exception.getMessage(), exception);
		} 
	}
	
	/**
	 * @return
	 */
	public int getCurrentConnectionIndex() {
		return currentConnectionIndex;
	}
}
