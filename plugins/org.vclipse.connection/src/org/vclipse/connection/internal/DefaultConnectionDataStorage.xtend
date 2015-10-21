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

import java.io.IOException
import java.util.ArrayList
import java.util.List
import org.eclipse.equinox.security.storage.ISecurePreferences
import org.eclipse.equinox.security.storage.StorageException
import org.eclipse.jface.preference.IPersistentPreferenceStore
import org.eclipse.jface.preference.IPreferenceStore
import org.vclipse.connection.IConnection
import org.vclipse.connection.IConnectionDataStorage
import org.vclipse.connection.IDestinationDataProvider
import org.vclipse.connection.VClipseConnectionPlugin
import com.google.inject.Inject

/** 
 */
class DefaultConnectionDataStorage implements IConnectionDataStorage {
	/** 
	 */
	public static final int BAD_INDEX = -1
	/** 
	 */
	static final String CURRENT_CONNECTION_INDEX = VClipseConnectionPlugin::ID + ".currentConnectionIndex"
	/** 
	 */
	static final String NUMBER_OF_CONNECTIONS = VClipseConnectionPlugin::ID + ".numberOfConnections"
	/** 
	 */
	final IPreferenceStore preferenceStore
	/** 
	 */
	final ISecurePreferences securePreferences
	/** 
	 */
	int currentConnectionIndex

	/** 
	 * @param preferenceStore
	 * @param securePreferences
	 */
	@Inject new(IPreferenceStore preferenceStore, ISecurePreferences securePreferences) {
		this.preferenceStore = preferenceStore
		this.securePreferences = securePreferences
		currentConnectionIndex = BAD_INDEX
	}

	/** 
	 * @return
	 */
	override List<IConnection> loadConnectionData(boolean loadPassword) {
		var List<IConnection> availableConnections = new ArrayList<IConnection>()
		currentConnectionIndex = preferenceStore.getInt(CURRENT_CONNECTION_INDEX)
		for (var int index = 0; index < preferenceStore.getInt(NUMBER_OF_CONNECTIONS); index++) {
			var IConnection newConnection = null
			if (loadPassword) {
				newConnection = new SimpleConnection()
			} else {
				newConnection = new IndexedConnection(index, securePreferences)
				try {
					newConnection.setPassword(securePreferences.get(IDestinationDataProvider::JCO_PASSWD, ""))
				} catch (StorageException exception) {
					VClipseConnectionPlugin::log(exception.getMessage(), exception)
				}

			}
			newConnection.setSystemName(preferenceStore.getString(IDestinationDataProvider::SYSTEM_NAME + index))
			newConnection.setSystemNumber(preferenceStore.getString(IDestinationDataProvider::JCO_SYSNR + index))
			newConnection.setHostName(preferenceStore.getString(IDestinationDataProvider::JCO_ASHOST + index))
			newConnection.setClientNumber(preferenceStore.getString(IDestinationDataProvider::JCO_CLIENT + index))
			newConnection.setUserName(preferenceStore.getString(IDestinationDataProvider::JCO_USER + index))
			newConnection.setLanguage(preferenceStore.getString(IDestinationDataProvider::JCO_LANG + index))
			availableConnections.add(newConnection)
		}
		return availableConnections
	}

	/** 
	 * @param connections
	 */
	override void storeConnectionData(List<IConnection> connections, int currentConnectionIndex) {
		preferenceStore.setValue(CURRENT_CONNECTION_INDEX, currentConnectionIndex)
		preferenceStore.setValue(NUMBER_OF_CONNECTIONS, connections.size())
		for (var int i = 0; i < connections.size(); i++) {
			val IConnection current = connections.get(i)
			preferenceStore.setValue(IDestinationDataProvider::JCO_ASHOST + i, current.getHostName())
			preferenceStore.setValue(IDestinationDataProvider::JCO_SYSNR + i, current.getSystemNumber())
			preferenceStore.setValue(IDestinationDataProvider::SYSTEM_NAME + i, current.getSystemName())
			preferenceStore.setValue(IDestinationDataProvider::JCO_CLIENT + i, current.getClientNumber())
			preferenceStore.setValue(IDestinationDataProvider::JCO_USER + i, current.getUserName())
			preferenceStore.setValue(IDestinationDataProvider::JCO_LANG + i, current.getLanguage())
			try {
				if (current instanceof AbstractConnection) {
					securePreferences.put(IDestinationDataProvider::JCO_PASSWD + i,
						(current as AbstractConnection).getPassword(), true)
				}

			} catch (StorageException exception) {
				VClipseConnectionPlugin::log(exception.getMessage(), exception)
			}

		}
		try {
			(preferenceStore as IPersistentPreferenceStore).save()
		} catch (IOException exception) {
			VClipseConnectionPlugin::log(exception.getMessage(), exception)
		}
		try {
			securePreferences.flush()
		} catch (IOException exception) {
			VClipseConnectionPlugin::log(exception.getMessage(), exception)
		}

	}

	/** 
	 * @return
	 */
	override int getCurrentConnectionIndex() {
		return currentConnectionIndex
	}

}
