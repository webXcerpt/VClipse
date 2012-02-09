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
package org.vclipse.configscan.impl;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.IConfigScanRunner;
import org.vclipse.connection.IConnection;
import org.vclipse.connection.IConnectionHandler;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

public class ConfigScanRunner implements IConfigScanRunner {

	private IConnectionHandler handler;
	
	@Inject
	public ConfigScanRunner(IConnectionHandler handler) {
		this.handler = handler;
	}
	
	public String execute(String output, RemoteConnection rc, String matNr, Map<String, Object> options) throws JCoException, CoreException {
		IConnection currentConnection = handler.getCurrentConnection();
		if (currentConnection==null) {
			throw new IllegalArgumentException("not connected");
		}
		JCoFunction function = handler.getJCoFunction("ZFSB_TEST_ENGINE_RFC");
        JCoParameterList importParameterList = function.getImportParameterList();
        importParameterList.setValue("IV_STOP_ON_ERROR", options.get(TestRun.STOP_ON_ERROR));
        importParameterList.setValue("IV_PERFORMANCE_RUN", options.get(TestRun.PERFORMANCE_RUN));
        importParameterList.setValue("IV_BREAKPOINT_ENABLED", options.get(TestRun.BREAKPOINT_ENABLED));
        importParameterList.setValue("IV_TEST_DATE", options.get(TestRun.TEST_DATE));
        importParameterList.setValue("IV_ROOT_QTY", options.get(TestRun.ROOT_QUANTITY));
        importParameterList.setValue("IV_IPC_KBID", options.get(TestRun.KBOBJECT));
        importParameterList.setValue("IV_TRACE_ALL", "X"); // "E" for only error messages
        importParameterList.setValue("IV_SHOW_LOG", "");
        importParameterList.setValue("IV_MAT_NAME", matNr);
        importParameterList.setValue("IV_ROOT_QTY", 1);
        importParameterList.setValue("IV_RFC_DEST", rc.getRfcDest());
        importParameterList.setValue("IV_REMOTE_SYSTEM", rc.getXcmScenario());
        importParameterList.setValue("IV_REMOTE_CLIENT", rc.getMandant());
        importParameterList.setValue("IV_LOGSYS", rc.getLogSys());
        importParameterList.setValue("IV_ENGINE_TYPE", rc.getEngine());
        importParameterList.setValue("IV_XML_STRING", output.getBytes());
        importParameterList.setValue("IV_XML_LOG_NEEDED", "X");
        importParameterList.setValue("IV_BREAKPOINT_ENABLED", "X");
		function.execute(handler.getJCoDestination());
		JCoParameterList exportParameterList = function.getExportParameterList();
		return new String(exportParameterList.getByteArray("EV_XML_LOG"));
	}
	
}
