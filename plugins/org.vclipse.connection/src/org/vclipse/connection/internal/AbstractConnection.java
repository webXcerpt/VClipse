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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.vclipse.connection.IConnection;
import org.vclipse.connection.VClipseConnectionPlugin;

/**
 *
 */
public abstract class AbstractConnection implements IConnection {

	/**
	 * 
	 */
	protected String systemName = ""; 
	protected String systemNumber = "";
	protected String hostName = "";
	protected String clientNumber = "";
	protected String userName = "";
	protected String password = "";
	protected String language = "";

	/**
	 * @return the systemName
	 */
	public abstract String getSystemName();

	/**
	 * @param systemName the systemName to set
	 */
	public abstract void setSystemName(String systemName);

	/**
	 * @return the systemNumber
	 */
	public abstract String getSystemNumber();

	/**
	 * @param systemNumber the systemNumber to set
	 */
	public abstract void setSystemNumber(String systemNumber);

	/**
	 * @return the hostName
	 */
	public abstract String getHostName();

	/**
	 * @param hostName the hostName to set
	 */
	public abstract void setHostName(String hostName);

	/**
	 * @return the clientNumber
	 */
	public abstract String getClientNumber();

	/**
	 * @param clientNumber the clientNumber to set
	 */
	public abstract void setClientNumber(String clientNumber);

	/**
	 * @return the userName
	 */
	public abstract String getUserName();

	/**
	 * @param userName the userName to set
	 */
	public abstract void setUserName(String userName);

	/**
	 * @return the password
	 */
	public abstract String getPassword();

	/**
	 * @param password the password to set
	 */
	public abstract void setPassword(String password);

	/**
	 * @return the language
	 */
	public abstract String getLanguage();

	/**
	 * @param language the language to set
	 */
	public abstract void setLanguage(String language);
	
	/**
	 * @return
	 */
	public IStatus readyToConnect() {
		if(hostName.isEmpty()) {
			return new Status(IStatus.ERROR, VClipseConnectionPlugin.ID, "Missing host name for the SAP connection!");
		} else if(systemNumber.isEmpty()) {
			return new Status(IStatus.ERROR, VClipseConnectionPlugin.ID, "Missing system number for the SAP connection!");
		} else if(clientNumber.isEmpty()) {
			return new Status(IStatus.ERROR, VClipseConnectionPlugin.ID, "Missing client number for the SAP connection!");
		} else if(userName.isEmpty()) {
			return new Status(IStatus.ERROR, VClipseConnectionPlugin.ID, "Missing user id for the SAP connection!");
		} else if(getPassword().isEmpty()) {
			return new Status(IStatus.ERROR, VClipseConnectionPlugin.ID, "Missing password");
		} else {
		
			return new Status(IStatus.OK, VClipseConnectionPlugin.ID, "Connection is ok.");
		}
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "\n[System name:" + systemName + "\nHost name:" + hostName + "\nSystem number:" + systemNumber + "\nClient number:" 
			+ clientNumber + "\nUser name:" + userName + "\nLanguage:" + language + "]";
	}
}
