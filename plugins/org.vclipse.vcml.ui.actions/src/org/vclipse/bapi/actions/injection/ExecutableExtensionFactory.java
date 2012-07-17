package org.vclipse.bapi.actions.injection;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.vclipse.bapi.actions.BAPIActionPlugin;

import com.google.inject.Injector;

public class ExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return BAPIActionPlugin.getInstance().getBundle();
	}

	@Override
	protected Injector getInjector() {
		return BAPIActionPlugin.getInstance().getInjector();
	}
}
