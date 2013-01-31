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
package org.vclipse.idoc2jcoidoc;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.vclipse.idoc2jcoidoc.injection.IDoc2JCoIDocModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class IDoc2JCoIDocPlugin extends AbstractUIPlugin {

	public static final String ID = "org.vclipse.idoc2jcoidoc";

	private static IDoc2JCoIDocPlugin plugin;
	
	private Injector injector;
	
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public Injector getInjector() {
		if(injector == null) {
			injector = Guice.createInjector(new IDoc2JCoIDocModule(this));
		}
		return injector;
	}
	
	public static IDoc2JCoIDocPlugin getInstance() {
		return plugin;
	}
	
	public static void log(final String message, final int severity) {
		getInstance().getLog().log(new Status(severity, ID, IStatus.OK, message, null));
	}

	public static void log(final String message, final Throwable throwable) {
		getInstance().getLog().log(new Status(IStatus.ERROR, ID, IStatus.OK, message, throwable));
	}
	
	public static ImageDescriptor getImageDescriptor(final String key) {
		return getInstance().getImageRegistry().getDescriptor(key);
	}
	
	public static Image getImage(final String key) {
		return getInstance().getImageRegistry().get(key);
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry registry) {
		// SAP model images
		addImage(IUiConstants.SEND_IDOCS_IMAGE, "icons/sendidocs.png");
		addImage(IUiConstants.SEND_IDOCS_IMAGE_DISABLED, "icons/sendidocs_disabled.png");
		addImage(IUiConstants.IDOC_DOCUMENT_IMAGE, "icons/idoc.gif");
		addImage(IUiConstants.IDOC_SEGMENT_IMAGE, "icons/page_white_text.png");
		super.initializeImageRegistry(registry);
	}
	
	private void addImage(final String name, final String path) {
		getInstance().getImageRegistry().put(name, imageDescriptorFromPlugin(ID, path).createImage());
	}
}
