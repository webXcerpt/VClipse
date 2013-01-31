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
package org.vclipse.tests;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.junit4.IInjectorProvider;
import org.eclipse.xtext.ui.shared.SharedStateModule;
import org.eclipse.xtext.util.Modules2;
import org.osgi.framework.BundleContext;
import org.vclipse.vcml.VCMLRuntimeModule;
import org.vclipse.vcml.ui.VCMLUiModule;
import org.vclipse.vcml.ui.internal.VCMLActivator;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Plug-in for JUnit tests being written for VClipse plug-ins.
 * 
 * This plug-in has an own injector, that is neither used elsewhere nor required until now.
 * 
 * Please note that each package in this plug-in contains tests for a particular VClipse plug-in. 
 * They should have an own injector configured and provided by the interface @{link IInjectorProvider}.
 * 
 * The packages with extension swtbot contain tests for execution with SWTBot. Please consult the
 * SWTBot Documentation for further information(@{link http://wiki.eclipse.org/SWTBot/UsersGuide }).
 */
public class VClipseTestPlugin extends AbstractUIPlugin implements IInjectorProvider {

	public static final String ID = "org.vclipse.tests";
	private static VClipseTestPlugin plugin;
	private Injector injector;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static VClipseTestPlugin getInstance() {
		return plugin;
	}

	@Override
	public Injector getInjector() {
		if(injector == null) {
			injector = 
					Guice.createInjector(
							Modules2.mixin(
									new VClipseTestModule(this), 
									new VCMLRuntimeModule(), 
									new SharedStateModule(), 
									new VCMLUiModule(VCMLActivator.getInstance()
							))
			);
		}
		return injector;
	}
}
