package org.vclipse.configscan;

import java.util.List;

import com.google.common.collect.Lists;
import com.sap.conn.jco.JCoException;

/**
 * mock class for reading ConfigScan remote configuration data from table ZFSB_REMOTE_ENG 
 */
public class MockConfigScanRemoteConnections implements IConfigScanRemoteConnections {

	public List<? extends RemoteConnection> readConfigScanRemoteConnections() throws JCoException {
		return Lists.newArrayList(
				new RemoteConnectionImpl("875", "IPC D75", "IPC", "D75", "D75CLNT875", "crmordermaintain_NSN", "IPC_CONFIGURATION_UI"),
						new RemoteConnectionImpl("875", "IPC D79", "IPC", "D79_879_TRUSTED", "D20CLNT005", "", ""),
								new RemoteConnectionImpl("875", "IPC P75", "IPC", "P75CLNT875", "P75CLNT875", "crmordermaintain_NSN", "IPC_CONFIGURATION_UI_P75"),
										new RemoteConnectionImpl("875", "IPC P79", "IPC", "P79_879_TRUSTED", "P20CLNT005", "", ""),
												new RemoteConnectionImpl("875", "IPC Q75", "IPC", "Q75CLNT875", "Q75CLNT875", "crmordermaintain_NSN", "IPC_CONFIGURATION_UI_Q75"),
														new RemoteConnectionImpl("875", "IPC Q79", "IPC", "Q79_879_TRUSTED", "P20CLNT005", "", ""),
																new RemoteConnectionImpl("875", "VC D75", "VC", "", "", "", ""),
																		new RemoteConnectionImpl("875", "VC P75", "VC", "P75CLNT875", "", "", ""),
																				new RemoteConnectionImpl("875", "VC Q75", "VC", "Q75CLNT875", "", "", "")
														);
	}
	
	public class RemoteConnectionImpl implements RemoteConnection {
		private String mandant;
		private String description;
		private String engine;
		private String rfcDest;
		private String logSys;
		private String xcmScenario;
		private String ipcConfigUi;
		
		public RemoteConnectionImpl(String mandant, String description,
				String engine, String rfcDest, String logSys,
				String xcmScenario, String ipcConfigUi) {
			super();
			this.mandant = mandant;
			this.description = description;
			this.engine = engine;
			this.rfcDest = rfcDest;
			this.logSys = logSys;
			this.xcmScenario = xcmScenario;
			this.ipcConfigUi = ipcConfigUi;
		}
		public String getMandant() {
			return mandant;
		}
		public String getDescription() {
			return description;
		}
		public String getEngine() {
			return engine;
		}
		public String getRfcDest() {
			return rfcDest;
		}
		public String getLogSys() {
			return logSys;
		}
		public String getXcmScenario() {
			return xcmScenario;
		}
		public String getIpcConfigUi() {
			return ipcConfigUi;
		}
	}

}
