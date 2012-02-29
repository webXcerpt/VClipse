package org.vclipse.sap.deployment;

import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.base.ui.BaseUiPlugin;
import org.vclipse.sap.deployment.injection.DeploymentModule;

import com.google.inject.Injector;

public class DeploymentPlugin extends BaseUiPlugin {

	static {
		ID = "org.vclipse.deployment";		
	}

	@Override
	public Injector getInjector(AbstractGenericModule optionalModule) {
		return super.getInjector(optionalModule == null ? new DeploymentModule(this) : optionalModule);
	}
}
