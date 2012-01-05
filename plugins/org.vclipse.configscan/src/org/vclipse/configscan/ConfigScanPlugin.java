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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.vclipse.configscan.injection.ConfigScanModule;

/**
 * The activator class controls the plug-in life cycle
 */
public class ConfigScanPlugin extends AbstractUIPlugin {

	public static final String ID = "org.vclipse.configscan"; //$NON-NLS-1$

	private static ConfigScanPlugin plugin;
	
	private Injector injector;

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

	public Injector getInjector() {
		if(injector == null) {
			injector = Guice.createInjector(new ConfigScanModule());
		}
		return injector;
	}
	
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(ID, path);
	}
	
	@Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        super.initializeImageRegistry(registry);
        Bundle bundle = Platform.getBundle(ID);

        
        ImageDescriptor success = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/s.gif"), null));
        ImageDescriptor failure = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/e.gif"), null));
        ImageDescriptor relaunch = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/relaunch.gif"), null));
        ImageDescriptor relaunchf = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/relaunchf.gif"), null));
        ImageDescriptor select_next = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/select_next.gif"), null));
        ImageDescriptor select_prev = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/select_prev.gif"), null));
        ImageDescriptor stop = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/stop.gif"), null));
        ImageDescriptor w = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/w.gif"), null));
        ImageDescriptor failures = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/failures.gif"), null));
        ImageDescriptor flatLayout = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/flatLayout.gif"), null));
        ImageDescriptor hierarchicalLayout = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/hierarchicalLayout.gif"), null));
        ImageDescriptor expandAll = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/expandall.gif"), null));
        ImageDescriptor collapseAll = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/collapseall.gif"), null));
        ImageDescriptor configscan = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/Fysbee.png"), null));
        ImageDescriptor cmlt = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/tests.png"), null));
        
        registry.put("s", success);
        registry.put("f", failure);
        registry.put("relaunch", relaunch);
        registry.put("relaunchf", relaunchf);
        registry.put("select_next", select_next);
        registry.put("select_prev", select_prev);
        registry.put("stop", stop);
        registry.put("w", w);
        registry.put("failures", failures);
        registry.put("flatLayout", flatLayout);
        registry.put("hierarchicalLayout", hierarchicalLayout);
        registry.put("expandAll", expandAll);
        registry.put("collapseAll", collapseAll);
        registry.put("configscan", configscan);
        registry.put("cmlt", cmlt);
    }

}
