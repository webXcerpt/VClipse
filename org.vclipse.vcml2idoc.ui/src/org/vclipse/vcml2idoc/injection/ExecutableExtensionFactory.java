/**
 * 
 */
package org.vclipse.vcml2idoc.injection;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.vclipse.vcml2idoc.VCML2IDocUIPlugin;

import com.google.inject.Injector;

/**
 * @author as
 *
 */
public class ExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	/**
	 * @see org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory#getBundle()
	 */
	@Override
	protected Bundle getBundle() {
		return VCML2IDocUIPlugin.getDefault().getBundle();
	}

	/**
	 * @see org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory#getInjector()
	 */
	@Override
	protected Injector getInjector() {
		return VCML2IDocUIPlugin.getDefault().getInjector();
	}
}
