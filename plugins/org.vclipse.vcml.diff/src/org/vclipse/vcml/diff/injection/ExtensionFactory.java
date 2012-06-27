/**
 * 
 */
package org.vclipse.vcml.diff.injection;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.vclipse.vcml.diff.VcmlDiffPlugin;

import com.google.inject.Injector;

/**
 *
 */
public class ExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return VcmlDiffPlugin.getDefault().getBundle();
	}

	@Override
	protected Injector getInjector() {
		return VcmlDiffPlugin.getDefault().getInjector();
	}

}
