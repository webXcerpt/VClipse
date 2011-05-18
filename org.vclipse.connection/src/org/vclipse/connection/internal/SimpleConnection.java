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


/**
 *
 */
public class SimpleConnection extends AbstractConnection {

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
		return password;
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
