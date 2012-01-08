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
package org.vclipse.configscan;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.vclipse.configscan.injection.ConfigScanModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * The activator class controls the plug-in life cycle
 */
public class ConfigScanPlugin extends AbstractUIPlugin {

	public static final String ID = "org.vclipse.configscan";

	private static ConfigScanPlugin plugin;
	
	private static final String ICON_PATH = "icons/";
	
	private Injector injector;

	public static ConfigScanPlugin getDefault() {
		return plugin;
	}
	
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(ID, path);
	}

	public static void log(String message, int severity, Throwable throwable) {
		getDefault().getLog().log(new Status(severity, ID, IStatus.OK, message, throwable));
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public Injector getInjector() {
		if(injector == null) {
			injector = Guice.createInjector(new ConfigScanModule(this));
		}
		return injector;
	}
	
	@Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        registry.put(IConfigScanImages.FAILURES, getDescriptor(IConfigScanImages.FAILURES));
        registry.put(IConfigScanImages.COLLAPSE_ALL, getDescriptor(IConfigScanImages.COLLAPSE_ALL));
        registry.put(IConfigScanImages.EXPAND_ALL, getDescriptor(IConfigScanImages.EXPAND_ALL));
        registry.put(IConfigScanImages.SUCCESS, getDescriptor(IConfigScanImages.SUCCESS));
        registry.put(IConfigScanImages.RELAUNCH, getDescriptor(IConfigScanImages.RELAUNCH));
        registry.put(IConfigScanImages.RELAUNCHF, getDescriptor(IConfigScanImages.RELAUNCHF));
        registry.put(IConfigScanImages.SELECT_NEXT, getDescriptor(IConfigScanImages.SELECT_NEXT));
        registry.put(IConfigScanImages.SELECT_PREV, getDescriptor(IConfigScanImages.SELECT_PREV));
        registry.put(IConfigScanImages.STOP, getDescriptor(IConfigScanImages.STOP));
        registry.put(IConfigScanImages.WARNING, getDescriptor(IConfigScanImages.WARNING));
        registry.put(IConfigScanImages.FLAT_LAYOUT, getDescriptor(IConfigScanImages.FLAT_LAYOUT));
        registry.put(IConfigScanImages.HIERARCHICAL_LAYOUT, getDescriptor(IConfigScanImages.HIERARCHICAL_LAYOUT));
        registry.put(IConfigScanImages.FYSBEE, getDescriptor(IConfigScanImages.FYSBEE));
        registry.put(IConfigScanImages.TESTS, getDescriptor(IConfigScanImages.TESTS));
        super.initializeImageRegistry(registry);
    }
	
	private ImageDescriptor getDescriptor(String imageName) {
		 return ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(ICON_PATH + imageName), null));
	}
}
