package org.vclipse.vcml.ui.actions.injection;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.vclipse.vcml.ui.actions.VcmlUiActionPlugin;

import com.google.inject.Injector;

public class ExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return VcmlUiActionPlugin.getDefault().getBundle();
	}

	@Override
	protected Injector getInjector() {
		return VcmlUiActionPlugin.getDefault().getInjector();
	}
}
