package org.vclipse.configscan.actions;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import com.google.inject.Inject;
import org.vclipse.configscan.IConfigScanRemoteConnections;

public class LaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

	@Inject
	private IConfigScanRemoteConnections remoteConnections;
	
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		setTabs(new ILaunchConfigurationTab[]{new OptionsLaunchConfigurationTab(remoteConnections)});
	}
}
