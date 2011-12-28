package org.vclipse.configscan;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.connection.IConnection;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.vcml.utils.ISapConstants;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

public class ConfigScanRunner implements IConfigScanRunner {

	@Inject
	private IConnectionHandler handler;
	
	@Inject
	private IPreferenceStoreAccess preferenceStoreAccess;
	
	public String execute(IFile file, String output, RemoteConnection rc, String matNr, String bomApplication) throws JCoException, CoreException {
		IConnection currentConnection = handler.getCurrentConnection();
		if (currentConnection==null) {
			throw new IllegalArgumentException("not connected");
		}
		IPreferenceStore preferenceStore = preferenceStoreAccess.getPreferenceStore();
		JCoFunction function = handler.getJCoFunction("ZFSB_TEST_ENGINE_RFC");
        JCoParameterList importParameterList = function.getImportParameterList();
        importParameterList.setValue("IV_TRACE_ALL", "X"); // "E" for only error messages
        importParameterList.setValue("IV_SHOW_LOG", "");
        importParameterList.setValue("IV_MAT_NAME", matNr);
        importParameterList.setValue("IV_PLANT", preferenceStore.getString(ISapConstants.PLANT));
        importParameterList.setValue("IV_BOM_APP", bomApplication);
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
