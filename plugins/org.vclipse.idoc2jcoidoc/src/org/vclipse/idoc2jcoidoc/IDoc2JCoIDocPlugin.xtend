/** 
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.idoc2jcoidoc

import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.jface.resource.ImageRegistry
import org.eclipse.swt.graphics.Image
import org.eclipse.ui.plugin.AbstractUIPlugin
import org.osgi.framework.BundleContext
import org.vclipse.idoc2jcoidoc.injection.IDoc2JCoIDocModule
import com.google.inject.Guice
import com.google.inject.Injector

class IDoc2JCoIDocPlugin extends AbstractUIPlugin {
	public static final String ID = "org.vclipse.idoc2jcoidoc"
	static IDoc2JCoIDocPlugin plugin
	Injector injector

	override void start(BundleContext context) throws Exception {
		super.start(context)
		plugin = this
	}

	override void stop(BundleContext context) throws Exception {
		plugin = null
		super.stop(context)
	}

	def Injector getInjector() {
		if (injector === null) {
			injector = Guice::createInjector(new IDoc2JCoIDocModule(this))
		}
		return injector
	}

	def static IDoc2JCoIDocPlugin getInstance() {
		return plugin
	}

	def static void log(String message, int severity) {
		getInstance().getLog().log(new Status(severity, ID, IStatus::OK, message, null))
	}

	def static void log(String message, Throwable throwable) {
		getInstance().getLog().log(new Status(IStatus::ERROR, ID, IStatus::OK, message, throwable))
	}

	def static ImageDescriptor getImageDescriptor(String key) {
		return getInstance().getImageRegistry().getDescriptor(key)
	}

	def static Image getImage(String key) {
		return getInstance().getImageRegistry().get(key)
	}

	override protected void initializeImageRegistry(ImageRegistry registry) {
		// SAP model images
		addImage(IUiConstants::SEND_IDOCS_IMAGE, "icons/sendidocs.png")
		addImage(IUiConstants::SEND_IDOCS_IMAGE_DISABLED, "icons/sendidocs_disabled.png")
		addImage(IUiConstants::IDOC_DOCUMENT_IMAGE, "icons/idoc.gif")
		addImage(IUiConstants::IDOC_SEGMENT_IMAGE, "icons/page_white_text.png")
		super.initializeImageRegistry(registry)
	}

	def private void addImage(String name, String path) {
		getInstance().getImageRegistry().put(name, imageDescriptorFromPlugin(ID, path).createImage())
	}

}
