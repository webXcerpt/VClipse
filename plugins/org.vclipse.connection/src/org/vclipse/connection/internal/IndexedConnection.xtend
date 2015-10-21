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

import org.eclipse.equinox.security.storage.ISecurePreferences
import org.eclipse.equinox.security.storage.StorageException
import org.vclipse.connection.IDestinationDataProvider
import org.vclipse.connection.VClipseConnectionPlugin

/** 
 */
final package class IndexedConnection extends AbstractConnection {
	/** 
	 */
	final int index
	/** 
	 */
	final ISecurePreferences securePreferences
	/** 
	 */
	boolean passwordLoaded

	/** 
	 * @param index
	 * @param securePreferences
	 */
	new(int index, ISecurePreferences securePreferences) {
		super()
		if (!(index > DefaultConnectionDataStorage::BAD_INDEX)) {
			throw new AssertionError()
		}
		this.index = index
		this.securePreferences = securePreferences
		passwordLoaded = false
	}

	override String getClientNumber() {
		return clientNumber
	}

	override String getHostName() {
		return hostName
	}

	override String getLanguage() {
		return language
	}

	override package String getPassword() {
		if (passwordLoaded) {
			return password
		} else {
			try {
				passwordLoaded = true
				password = securePreferences.get(IDestinationDataProvider::JCO_PASSWD + index, "")
			} catch (StorageException exception) {
				VClipseConnectionPlugin::log(exception.getMessage(), exception)
			}
			return password
		}
	}

	override String getSystemName() {
		return systemName
	}

	override String getSystemNumber() {
		return systemNumber
	}

	override String getUserName() {
		return userName
	}

	override void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber
	}

	override void setHostName(String hostName) {
		this.hostName = hostName
	}

	override void setLanguage(String language) {
		this.language = language
	}

	override void setPassword(String password) {
		this.password = password
	}

	override void setSystemName(String systemName) {
		this.systemName = systemName
	}

	override void setSystemNumber(String systemNumber) {
		this.systemNumber = systemNumber
	}

	override void setUserName(String userName) {
		this.userName = userName
	}

}
