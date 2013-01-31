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
package org.vclipse.connection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.vclipse.connection.dialogs.IDocErrorDialog;
import org.vclipse.connection.dialogs.JCoErrorDialog;
import org.vclipse.connection.injection.ConnectionModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 *
 */
public class VClipseConnectionPlugin extends AbstractUIPlugin {

	/**
	 * Plug-in id
	 */
	public static final String ID = "org.vclipse.connection";

	/**
	 * Shared instance
	 */
	private static VClipseConnectionPlugin plugin;
	
	/**
	 * Boolean value indicating if JCo-Library is available 
	 */
	private boolean jCoAvailable;
	
	/**
	 * Boolean value indicating if IDoc-Library is available
	 */
	private boolean iDocAvailable;
	
	/**
	 * 
	 */
	private Injector injector;
	
	/**
	 * @return the shared instance
	 */
	public static VClipseConnectionPlugin getDefault() {
		return plugin;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		// check if JCo-Library is available
		try {
			getClass().getClassLoader().loadClass("com.sap.conn.jco.JCo");
			jCoAvailable = true;
		} catch(ClassNotFoundException e) {
			jCoAvailable = false;
			new JCoErrorDialog().open();
		}
		
		// check if IDoc-Library is available
		try {
			getClass().getClassLoader().loadClass("com.sap.conn.idoc.IDocFactory");
			iDocAvailable = true;
		} catch(ClassNotFoundException e) {
			iDocAvailable = false;
			new IDocErrorDialog().open();
		}
		
		super.start(context);
		plugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * @return
	 */
	public Injector getInjector() {
		if(injector == null) {
			injector = Guice.createInjector(new ConnectionModule(this));
		}
		return injector;
	}
			
	/**
	 * @return
	 */
	public boolean isJCoAvailable() {
		return jCoAvailable;
	}

	/**
	 * @return
	 */
	public boolean isIDocAvailable() {
		return iDocAvailable;
	}
	
	/**
	 * @param message
	 * @param severity
	 */
	public static void log(final String message, final int severity) {
		getDefault().getLog().log(new Status(severity, ID, IStatus.OK, message, null));
	}

	/**
	 * @param message
	 * @param throwable
	 */
	public static void log(final String message, final Throwable throwable) {
		getDefault().getLog().log(new Status(IStatus.ERROR, ID, IStatus.OK, message, throwable));
	}
	
	/**
	 * @param key
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(final String key) {
		return getDefault().getImageRegistry().getDescriptor(key);
	}
	
	/**
	 * @param key
	 * @return
	 */
	public static Image getImage(final String key) {
		return getDefault().getImageRegistry().get(key);
	}
	
	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(final ImageRegistry registry) {
		// Connection state image
		addImage(ISharedImages.CONNECTED_IMAGE, "icons/connection_enabled.png");
		addImage(ISharedImages.DISCONNECTED_IMAGE, "icons/connection_disabled.png");
		super.initializeImageRegistry(registry);
	}
	
	/**
	 * @param name
	 * @param path
	 */
	private void addImage(final String name, final String path) {
		getDefault().getImageRegistry().put(name, imageDescriptorFromPlugin(ID, path).createImage());
	}
}
