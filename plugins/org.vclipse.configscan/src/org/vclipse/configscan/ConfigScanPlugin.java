package org.vclipse.configscan;
/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.vclipse.configscan.injection.ConfigScanModule;
import org.vclipse.configscan.views.TestRunsHistory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * The activator class controls the plug-in life cycle
 */
public class ConfigScanPlugin extends AbstractUIPlugin {

	public static final String ID = "org.vclipse.configscan";

	private static ConfigScanPlugin plugin;
	
	private Injector injector;
	
	@Inject
	private TestRunsHistory history;

	public static ConfigScanPlugin getDefault() {
		return plugin;
	}
	
	public static void log(String message, int severity, Throwable throwable) {
		getDefault().getLog().log(new Status(severity, ID, IStatus.OK, message, throwable));
	}
	
	public static void log(String message, int severity) {
		getDefault().getLog().log(new Status(severity, ID, message));
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		if(getPreferenceStore().getBoolean(IConfigScanConfiguration.SAVE_HISTORY)) {
			history.save();			
		}
		super.stop(context);
	}

	public Injector getInjector() {
		if(injector == null) {
			injector = Guice.createInjector(new ConfigScanModule(this));
		}
		return injector;
	}
}
