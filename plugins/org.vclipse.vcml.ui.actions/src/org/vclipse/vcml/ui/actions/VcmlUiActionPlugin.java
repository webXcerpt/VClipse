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
package org.vclipse.vcml.ui.actions;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.vclipse.vcml.ui.actions.injection.ActionModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class VcmlUiActionPlugin extends AbstractUIPlugin {

	public static final String ID = "org.vclipse.vcml.ui.actions";

	private static VcmlUiActionPlugin plugin;

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

	public static VcmlUiActionPlugin getDefault() {
		return plugin;
	}
	
	public Injector getInjector() {
		if(injector == null) {
			injector = Guice.createInjector(new ActionModule());
		}
		return injector;
	}
}
