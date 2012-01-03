/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.diff;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class VcmlDiffPlugin extends AbstractUIPlugin {

	public static final String ID = "org.vclipse.vcml.diff";

	private static VcmlDiffPlugin plugin;

	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static VcmlDiffPlugin getDefault() {
		return plugin;
	}
	
	public static void log(final String message, final int severity) {
		getDefault().getLog().log(new Status(severity, ID, IStatus.OK, message, null));
	}

	public static void log(final String message, final Throwable throwable) {
		getDefault().getLog().log(new Status(IStatus.ERROR, ID, IStatus.OK, message, throwable));
	}
}
