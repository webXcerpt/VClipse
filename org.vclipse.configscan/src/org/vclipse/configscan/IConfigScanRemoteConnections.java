package org.vclipse.configscan;

import java.util.List;

import com.google.inject.ImplementedBy;
import com.sap.conn.jco.JCoException;

/**
 * reads ConfigScan remote configuration data from table ZFSB_REMOTE_ENG 
 */
@ImplementedBy(ConfigScanRemoteConnections.class)
public interface IConfigScanRemoteConnections {

	public List<? extends RemoteConnection> readConfigScanRemoteConnections() throws JCoException;
	
	public interface RemoteConnection {
		public String getMandant();
		public String getDescription();
		public String getEngine();
		public String getRfcDest();
		public String getLogSys();
		public String getXcmScenario();
		public String getIpcConfigUi();
	}

}
