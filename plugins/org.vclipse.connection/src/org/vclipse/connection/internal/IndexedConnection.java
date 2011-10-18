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

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.vclipse.connection.IDestinationDataProvider;
import org.vclipse.connection.VClipseConnectionPlugin;

/**
 *
 */
final class IndexedConnection extends AbstractConnection {

	/**
	 * 
	 */
	private final int index;
	
	/**
	 * 
	 */
	private final ISecurePreferences securePreferences;
	
	/**
	 * 
	 */
	private boolean passwordLoaded;
	
	/**
	 * @param index
	 * @param securePreferences
	 */
	public IndexedConnection(final int index, final ISecurePreferences securePreferences) {
		super();
		assert index > DefaultConnectionDataStorage.BAD_INDEX;
		this.index = index;
		this.securePreferences = securePreferences;
		passwordLoaded = false;
	}

	@Override
	public String getClientNumber() {
		return clientNumber;
	}

	@Override
	public String getHostName() {
		return hostName;
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public String getPassword() {
		if(passwordLoaded) {
			return password;
		} else {
			try {
				passwordLoaded = true;
				password = securePreferences.get(IDestinationDataProvider.JCO_PASSWD + index, "");
			} catch(final StorageException exception) {
				VClipseConnectionPlugin.log(exception.getMessage(), exception);
			}
			return password;
		}
	}

	@Override
	public String getSystemName() {
		return systemName;
	}

	@Override
	public String getSystemNumber() {
		return systemNumber;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public void setClientNumber(final String clientNumber) {
		this.clientNumber = clientNumber;
	}

	@Override
	public void setHostName(final String hostName) {
		this.hostName = hostName;
	}

	@Override
	public void setLanguage(final String language) {
		this.language = language;
	}

	@Override
	public void setPassword(final String password) {
		this.password = password;
	}

	@Override
	public void setSystemName(final String systemName) {
		this.systemName = systemName;
	}

	@Override
	public void setSystemNumber(final String systemNumber) {
		this.systemNumber = systemNumber;
	}

	@Override
	public void setUserName(final String userName) {
		this.userName = userName;
	}
}
