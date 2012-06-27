/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.base;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class BasePlugin extends Plugin {

	public static final String ID = "org.vclipse.base";
	
	private static BasePlugin plugin;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public static BasePlugin getInstance() {
		return plugin;
	}

	public static void log(String message, int severity) {
		log(message, severity, null);
	}
	
	public static void log(String message, Throwable throwable) {
		log(message, IStatus.ERROR, throwable);
	}
	
	public static void log(String message, int severity, Throwable throwable) {
		getInstance().getLog().log(throwable == null ? new Status(severity, ID, message) : new Status(severity, ID, IStatus.OK, message, throwable));
	}
}
