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
package org.vclipse.vcml2idoc;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.vclipse.vcml2idoc.injection.VCML2IDocModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * The activator class controls the plug-in life cycle
 */
public class VCML2IDocPlugin extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String ID = "org.vclipse.vcml2idoc.ui";

	/**
	 *  The shared instance
	 */
	private static VCML2IDocPlugin plugin;
	
	/**
	 * 
	 */
	private Injector injector;

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static VCML2IDocPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * @return
	 */
	public Injector getInjector() {
		if(injector == null) {
			injector = Guice.createInjector(new VCML2IDocModule(this));
		}
		return injector;
	}
	
	/**
	 * @param message
	 * @param severity
	 */
	public static void log(final String message, final int severity) {
		getDefault().getLog().log(new Status(severity, ID, IStatus.OK, message, null));
	}

	/**
	 * @param message
	 * @param throwable
	 */
	public static void log(final String message, final Throwable throwable) {
		getDefault().getLog().log(new Status(IStatus.ERROR, ID, IStatus.OK, message, throwable));
	}
}
