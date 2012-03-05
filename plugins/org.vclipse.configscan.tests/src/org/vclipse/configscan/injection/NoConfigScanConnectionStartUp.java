package org.vclipse.configscan.injection;

import org.eclipse.ui.IStartup;
import org.vclipse.configscan.ConfigScanPlugin;

public class NoConfigScanConnectionStartUp implements IStartup {

	@Override
	public void earlyStartup() {
		ConfigScanPlugin plugin = ConfigScanPlugin.getDefault();
		TestConfigScanModule testModule = new TestConfigScanModule(plugin);
		plugin.getInjector(testModule);
	}
}
