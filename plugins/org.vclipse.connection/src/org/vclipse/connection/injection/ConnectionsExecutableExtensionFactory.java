/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.connection.injection;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.vclipse.connection.VClipseConnectionPlugin;

import com.google.inject.Injector;

/**
 *
 */
public class ConnectionsExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	/**
	 * @see org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory#getBundle()
	 */
	@Override
	protected Bundle getBundle() {
		return VClipseConnectionPlugin.getDefault().getBundle();
	}

	/**
	 * @see org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory#getInjector()
	 */
	@Override
	protected Injector getInjector() {
		return VClipseConnectionPlugin.getDefault().getInjector();
	}

}
