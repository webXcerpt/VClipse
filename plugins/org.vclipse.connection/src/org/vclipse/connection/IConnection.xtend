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
package org.vclipse.connection

import org.eclipse.core.runtime.IStatus

/** 
 */
interface IConnection {
	/** 
	 * @return the systemName
	 */
	def String getSystemName()

	/** 
	 * @param systemName the systemName to set
	 */
	def void setSystemName(String systemName)

	/** 
	 * @return the systemNumber
	 */
	def String getSystemNumber()

	/** 
	 * @param systemNumber the systemNumber to set
	 */
	def void setSystemNumber(String systemNumber)

	/** 
	 * @return the hostName
	 */
	def String getHostName()

	/** 
	 * @param hostName the hostName to set
	 */
	def void setHostName(String hostName)

	/** 
	 * @return the clientNumber
	 */
	def String getClientNumber()

	/** 
	 * @param clientNumber the clientNumber to set
	 */
	def void setClientNumber(String clientNumber)

	/** 
	 * @return the userName
	 */
	def String getUserName()

	/** 
	 * @param userName the userName to set
	 */
	def void setUserName(String userName)

	/** 
	 * @param password the password to set
	 */
	def void setPassword(String password)

	/** 
	 * @return the language
	 */
	def String getLanguage()

	/** 
	 * @param language the language to set
	 */
	def void setLanguage(String language)

	/** 
	 * @return
	 */
	def IStatus readyToConnect()

}
