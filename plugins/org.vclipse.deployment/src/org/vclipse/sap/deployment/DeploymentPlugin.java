package org.vclipse.sap.deployment;

import org.osgi.framework.BundleContext;
import org.vclipse.base.ui.BaseUiPlugin;
import org.vclipse.sap.deployment.injection.DeploymentModule;

public class DeploymentPlugin extends BaseUiPlugin {

	static {
		ID = "org.vclipse.deployment";		
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		injectionModule = new DeploymentModule(this);
	}
}
