/**
 * 
 */
package org.vclipse.idoc2jcoidoc.injection;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.vclipse.idoc2jcoidoc.Activator;

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
		return Activator.getDefault().getBundle();
	}

	/**
	 * @see org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory#getInjector()
	 */
	@Override
	protected Injector getInjector() {
		return Activator.getDefault().getInjector();
	}

}
