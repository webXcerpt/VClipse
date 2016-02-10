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

import java.util.Properties
import org.vclipse.connection.IConnection
import org.vclipse.connection.IConnectionHandler
import org.vclipse.connection.IDestinationDataProvider
import com.google.inject.Inject
import com.sap.conn.jco.ext.DestinationDataEventListener

/** 
 */
class DefaultDestinationDataProvider implements IDestinationDataProvider {
	/** 
	 */
	final IConnectionHandler handler

	/** 
	 * @param connectionHandler
	 */
	@Inject new(IConnectionHandler connectionHandler) {
		handler = connectionHandler
	}

	/** 
	 * @see com.sap.conn.jco.ext.DestinationDataProvider#getDestinationProperties(java.lang.String)
	 */
	override Properties getDestinationProperties(String name) {
		val IConnection connection = handler.getCurrentConnection()
		if (connection === null) {
			return null
		} else {
			val Properties properties = new Properties()
			properties.setProperty(SYSTEM_NAME, connection.getSystemName())
			properties.setProperty(JCO_SYSNR, connection.getSystemNumber())
			properties.setProperty(JCO_ASHOST, connection.getHostName())
			properties.setProperty(JCO_CLIENT, connection.getClientNumber())
			properties.setProperty(JCO_SAPROUTER, connection.getRouter())
			properties.setProperty(JCO_USER, connection.getUserName())
			if (connection instanceof AbstractConnection) {
				properties.setProperty(JCO_PASSWD, (connection as AbstractConnection).getPassword())
			}
			properties.setProperty(JCO_LANG, connection.getLanguage())
			return properties
		}
	}

	/** 
	 * @see com.sap.conn.jco.ext.DestinationDataProvider#setDestinationDataEventListener(com.sap.conn.jco.ext.DestinationDataEventListener)
	 */
	override void setDestinationDataEventListener(DestinationDataEventListener listener) {
		// not needed
	}

	/** 
	 * @see com.sap.conn.jco.ext.DestinationDataProvider#supportsEvents()
	 */
	override boolean supportsEvents() {
		return false
	}

}
