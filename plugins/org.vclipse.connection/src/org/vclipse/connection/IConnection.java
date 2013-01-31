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

import org.eclipse.core.runtime.IStatus;

/**
 *
 */
public interface IConnection {

	/**
	 * @return the systemName
	 */
	public String getSystemName();

	/**
	 * @param systemName the systemName to set
	 */
	public void setSystemName(String systemName);

	/**
	 * @return the systemNumber
	 */
	public String getSystemNumber();

	/**
	 * @param systemNumber the systemNumber to set
	 */
	public void setSystemNumber(String systemNumber);

	/**
	 * @return the hostName
	 */
	public String getHostName();

	/**
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName);

	/**
	 * @return the clientNumber
	 */
	public String getClientNumber();

	/**
	 * @param clientNumber the clientNumber to set
	 */
	public void setClientNumber(String clientNumber);

	/**
	 * @return the userName
	 */
	public String getUserName();

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName);

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password);

	/**
	 * @return the language
	 */
	public String getLanguage();

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language);
	
	/**
	 * @return
	 */
	public IStatus readyToConnect();
}
