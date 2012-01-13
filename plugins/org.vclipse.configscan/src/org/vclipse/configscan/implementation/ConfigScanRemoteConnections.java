/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.configscan.implementation;

import java.util.List;

import org.vclipse.configscan.IConfigScanRemoteConnections;
import org.vclipse.connection.IConnection;
import org.vclipse.connection.IConnectionHandler;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecordField;
import com.sap.conn.jco.JCoRecordFieldIterator;
import com.sap.conn.jco.JCoTable;

/**
 * reads ConfigScan remote configuration data from table ZFSB_REMOTE_ENG 
 */
public class ConfigScanRemoteConnections implements IConfigScanRemoteConnections {

	@Inject
	private IConnectionHandler handler;
	
	public List<RemoteConnection> readConfigScanRemoteConnections() throws JCoException {
		IConnection currentConnection = handler.getCurrentConnection();
		if (currentConnection==null) {
			throw new IllegalArgumentException("not connected");
		}
		JCoFunction function = handler.getJCoFunction("RFC_READ_TABLE");
		JCoParameterList importParameterList = function.getImportParameterList();
		importParameterList.setValue("QUERY_TABLE", "ZFSB_REMOTE_ENG");
		function.execute(handler.getJCoDestination());
		JCoTable table = function.getTableParameterList().getTable("DATA");
		List<RemoteConnection> result = Lists.newArrayList();
		if (table.getNumRows() > 0) {
			do {
				for (JCoRecordFieldIterator fI = table.getRecordFieldIterator(); fI.hasNextField();) {
					JCoRecordField tabField = fI.nextRecordField();
					result.add(new RemoteConnectionImpl(tabField.getString()));
				}
			} while (table.nextRow());
		}
		return result;
	}
	
	public class RemoteConnectionImpl implements RemoteConnection {
		String tableData;
		public RemoteConnectionImpl (String tableData) {
			this.tableData = tableData + "                                                                                                                        ";
		}
		public String getMandant() {
			return tableData.substring(0,3).trim();
		}
		public String getDescription() {
			return tableData.substring(3,53).trim();
		}
		public String getEngine() {
			return tableData.substring(53,56).trim();
		}
		public String getRfcDest() {
			return tableData.substring(56,88).trim();
		}
		public String getLogSys() {
			return tableData.substring(88,98).trim();
		}
		public String getXcmScenario() {
			return tableData.substring(98,118).trim();
		}
		public String getIpcConfigUi() {
			return tableData.substring(118).trim();
		}
		public String toString() {
			return "RemoteConnection " + getDescription()
					+ " [mandant: " + getMandant() + ", "
					+ "engine: " + getEngine() + ", "
					+ "rfcdest: " + getRfcDest() + ", "
					+ "logsys: " + getLogSys() + ", "
					+ "xcmscenario: " + getXcmScenario() + ", "
					+ "ipcconfigui: " + getIpcConfigUi() + "]";
		}
		
	}

}
