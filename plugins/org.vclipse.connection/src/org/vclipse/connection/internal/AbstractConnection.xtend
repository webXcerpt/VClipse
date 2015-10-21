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

import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
import org.vclipse.connection.IConnection
import org.vclipse.connection.VClipseConnectionPlugin

/** 
 */
abstract class AbstractConnection implements IConnection {
	/** 
	 */
	protected String systemName = ""
	protected String systemNumber = ""
	protected String hostName = ""
	protected String clientNumber = ""
	protected String userName = ""
	protected String password = ""
	protected String language = ""

	/** 
	 * @return the systemName
	 */
	override abstract String getSystemName()

	/** 
	 * @param systemName the systemName to set
	 */
	override abstract void setSystemName(String systemName)

	/** 
	 * @return the systemNumber
	 */
	override abstract String getSystemNumber()

	/** 
	 * @param systemNumber the systemNumber to set
	 */
	override abstract void setSystemNumber(String systemNumber)

	/** 
	 * @return the hostName
	 */
	override abstract String getHostName()

	/** 
	 * @param hostName the hostName to set
	 */
	override abstract void setHostName(String hostName)

	/** 
	 * @return the clientNumber
	 */
	override abstract String getClientNumber()

	/** 
	 * @param clientNumber the clientNumber to set
	 */
	override abstract void setClientNumber(String clientNumber)

	/** 
	 * @return the userName
	 */
	override abstract String getUserName()

	/** 
	 * @param userName the userName to set
	 */
	override abstract void setUserName(String userName)

	/** 
	 * @return the password
	 */
	def abstract package String getPassword()

	/** 
	 * @param password the password to set
	 */
	override abstract void setPassword(String password)

	/** 
	 * @return the language
	 */
	override abstract String getLanguage()

	/** 
	 * @param language the language to set
	 */
	override abstract void setLanguage(String language)

	/** 
	 * @return
	 */
	override IStatus readyToConnect() {
		if (hostName.isEmpty()) {
			return new Status(IStatus.ERROR, VClipseConnectionPlugin.ID, "Missing host name for the SAP connection!")
		} else if (systemNumber.isEmpty()) {
			return new Status(IStatus.ERROR, VClipseConnectionPlugin.ID,
				"Missing system number for the SAP connection!")
		} else if (clientNumber.isEmpty()) {
			return new Status(IStatus.ERROR, VClipseConnectionPlugin.ID,
				"Missing client number for the SAP connection!")
		} else if (userName.isEmpty()) {
			return new Status(IStatus.ERROR, VClipseConnectionPlugin.ID, "Missing user id for the SAP connection!")
		} else if (getPassword().isEmpty()) {
			return new Status(IStatus.ERROR, VClipseConnectionPlugin.ID, "Missing password")
		} else {
			return new Status(IStatus.OK, VClipseConnectionPlugin.ID, "Connection is ok.")
		}
	}

	/** 
	 * @see java.lang.Object#toString()
	 */
	override String toString() {
		return '''
[System name:«systemName»
Host name:«hostName»
System number:«systemNumber»
Client number:«clientNumber»
User name:«userName»
Language:«language»]'''
	}

}
