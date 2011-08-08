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
package org.vclipse.idoc2jcoidoc;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.vclipse.idoc2jcoidoc.injection.Module;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	/**
	 *  The plug-in ID
	 */
	public static final String ID = "org.vclipse.idoc2jcoidoc";

	/**
	 * The shared instance
	 */
	private static Activator plugin;
	
	/**
	 * 
	 */
	private Injector injector;
	
	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
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
			injector = Guice.createInjector(new Module(this));
		}
		return injector;
	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
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
		// SAP model images
		addImage(IUiConstants.SEND_IDOCS_IMAGE, "icons/sendidocs.png");
		addImage(IUiConstants.SEND_IDOCS_IMAGE_DISABLED, "icons/sendidocs_disabled.png");
		addImage(IUiConstants.IDOC_DOCUMENT_IMAGE, "icons/idoc.gif");
		addImage(IUiConstants.IDOC_SEGMENT_IMAGE, "icons/page_white_text.png");
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
