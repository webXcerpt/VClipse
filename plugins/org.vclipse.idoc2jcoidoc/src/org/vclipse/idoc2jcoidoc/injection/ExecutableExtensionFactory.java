/**
 * 
 */
package org.vclipse.idoc2jcoidoc.injection;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;

import com.google.inject.Injector;

/**
 *
 */
public class ExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return IDoc2JCoIDocPlugin.getDefault().getBundle();
	}

	@Override
	protected Injector getInjector() {
		return IDoc2JCoIDocPlugin.getDefault().getInjector();
	}

}
