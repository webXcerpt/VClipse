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
package org.vclipse.configscan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.osgi.framework.BundleContext;
import org.vclipse.base.ui.ErrorDialog;
import org.vclipse.configscan.injection.ConfigScanModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ConfigScanPlugin extends AbstractUIPlugin {

	public static String ID = "org.vclipse.configscan"; 

	protected static ConfigScanPlugin plugin;
	
	protected Injector injector;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public static ConfigScanPlugin getDefault() {
		return plugin;
	}
	
	public static Image getImage(String key) {
		return getDefault().getImageRegistry().get(key);
	}
	
	public Injector getInjector(AbstractGenericModule optionalModule) {
		if(injector == null) {
			injector = optionalModule == null ? Guice.createInjector(new ConfigScanModule(this)) : Guice.createInjector(optionalModule);
		}
		return injector;
	}

	public static void showErrorDialog(String dialogTitle, String message, IStatus status) {
		showErrorDialog(null, dialogTitle, message, status);
	}
	
	public static void showErrorDialog(Exception exception, String dialogTitle, String message) {
		showErrorDialog(exception, dialogTitle, message, null);
	}
	
	public static void showErrorDialog(final Exception exception, final String dialogTitle, final String message, final IStatus status) {
		log(message, null);
		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				new ErrorDialog(display.getActiveShell(), dialogTitle, message, status).open();
			}
		});
	}
	
	public static void log(String message, int severity) {
		log(message, severity, null);
	}
	
	public static void log(String message, Throwable throwable) {
		log(message, IStatus.ERROR, throwable);
	}
	
	public static void log(String message, int severity, Throwable throwable) {
		getDefault().getLog().log(throwable == null ? 
				new Status(severity, ID, message) : 
					new Status(severity, ID, IStatus.OK, message, throwable));
	}
}
