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
package org.vclipse.vcml.compare;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.shared.internal.SharedModule;
import org.eclipse.xtext.util.Modules2;
import org.osgi.framework.BundleContext;
import org.vclipse.vcml.compare.injection.VCMLCompareModule;
import org.vclipse.vcml.ui.VCMLUiModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 */
public class VCMLComparePlugin extends AbstractUIPlugin {

	public static final String ID = "org.vclipse.vcml.compare"; //$NON-NLS-1$

	private static VCMLComparePlugin plugin;
	
	private Injector injector;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Return the shared instance.
	 */
	public static VCMLComparePlugin getInstance() {
		return plugin;
	}

	/**
	 * Return the configured injector for this plug-in.
	 */
	public Injector getInjector() {
		if(injector == null) {
			injector = Guice.createInjector(
				Modules2.mixin(
						new VCMLCompareModule(this), 
						new SharedModule(), 
						new VCMLUiModule(this)
				)
			);
		}
		return injector;
	}
	
	/**
	 * Log a message with a given status in the error log view.
	 */
	public static void log(int status, String message) {
		getInstance().getLog().log(new Status(status, ID, message));
	}
}
