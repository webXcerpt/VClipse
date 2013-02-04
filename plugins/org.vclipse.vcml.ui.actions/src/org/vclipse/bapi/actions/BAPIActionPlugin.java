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
package org.vclipse.bapi.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.vclipse.bapi.actions.injection.ActionModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class BAPIActionPlugin extends AbstractUIPlugin {

	public static final String ID = "org.vclipse.vcml.ui.actions";

	private static BAPIActionPlugin plugin;

	private Injector injector;
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static BAPIActionPlugin getInstance() {
		return plugin;
	}
	
	public Injector getInjector() {
		if(injector == null) {
			injector = Guice.createInjector(new ActionModule());
		}
		return injector;
	}
	
	public static void log(int status, String message) {
		getInstance().getLog().log(new Status(status, ID, message));
	}
	
	public static void log(final IStatus status) {
		getInstance().getLog().log(status);
	}
	
	public static void log(final String message, final Throwable thr) {
		log(new Status(IStatus.ERROR, ID, IStatus.ERROR, message, thr));		
	}
}
