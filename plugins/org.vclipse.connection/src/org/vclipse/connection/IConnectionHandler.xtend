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

import java.io.IOException
import org.eclipse.core.runtime.IStatus
import org.eclipse.equinox.security.storage.StorageException
import org.osgi.service.prefs.BackingStoreException
import com.sap.conn.idoc.IDocRepository
import com.sap.conn.jco.JCoDestination
import com.sap.conn.jco.JCoException
import com.sap.conn.jco.JCoFunction
import com.sap.conn.jco.JCoFunctionTemplate
import com.sap.conn.jco.JCoRepository

/** 
 */
interface IConnectionHandler {
	/** 
	 * @param connection
	 */
	def void addConnection(IConnection connection)

	/** 
	 * @param connection
	 */
	def void removeConnection(IConnection connection)

	/** 
	 * @param listener
	 */
	def void addConnectionListener(IConnectionListener listener)

	/** 
	 * @param listener
	 */
	def void removeConnectionListener(IConnectionListener listener)

	/** 
	 * @param connection
	 * @param monitor
	 * @throws JCoException 
	 */
	def IStatus connect(IConnection connection) throws JCoException

	/** 
	 */
	def void disconnect()

	/** 
	 * @return
	 */
	def IConnection[] getAvailableConnections()

	/** 
	 * @return
	 */
	def IConnection getCurrentConnection()

	/** 
	 * @return
	 * @throws JCoException 
	 */
	def JCoDestination getJCoDestination() throws JCoException

	/** 
	 * @return
	 * @throws JCoException 
	 */
	def JCoRepository getJCoRepository() throws JCoException

	/** 
	 * @return
	 * @throws JCoException 
	 */
	def JCoFunction getJCoFunction(String name) throws JCoException

	/** 
	 * @return
	 * @throws JCoException 
	 */
	def JCoFunctionTemplate getJCoFunctionTemplate(String name) throws JCoException

	/** 
	 * @return
	 * @throws JCoException 
	 */
	def IDocRepository getIDocRepository() throws JCoException

	/** 
	 * @throws BackingStoreException 
	 * @throws IOException 
	 * @throws StorageException 
	 */
	def void storeConnectionData()

}
