/**
 * 
 */
package org.vclipse.sap.deployment.injection;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.vclipse.sap.deployment.DeploymentPlugin;

import com.google.inject.Injector;

/**
 *
 */
public class ExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return DeploymentPlugin.getDefault().getBundle();
	}

	@Override
	protected Injector getInjector() {
		return DeploymentPlugin.getDefault().getInjector();
	}

}
