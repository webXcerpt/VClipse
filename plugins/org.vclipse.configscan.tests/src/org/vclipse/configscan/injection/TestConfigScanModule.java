package org.vclipse.configscan.injection;

import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanRemoteConnections;
import org.vclipse.configscan.IConfigScanRunner;

public class TestConfigScanModule extends ConfigScanModule {

	public TestConfigScanModule(ConfigScanPlugin plugin) {
		super(plugin);
	}

	public Class<? extends IConfigScanRemoteConnections> bindConfigScanRemoteConnections() {
		return MockConfigScanRemoteConnections.class;
	}

	public Class<? extends IConfigScanRunner> bindConfigScanRunner() {
		return MockConfigScanRunner.class;
	}
}
