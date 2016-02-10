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
package org.vclipse.sap.deployment

import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.Display
import org.eclipse.ui.plugin.AbstractUIPlugin
import org.eclipse.xtext.service.AbstractGenericModule
import org.osgi.framework.BundleContext
import org.vclipse.base.ui.ErrorDialog
import org.vclipse.sap.deployment.injection.DeploymentModule
import com.google.inject.Guice
import com.google.inject.Injector

class DeploymentPlugin extends AbstractUIPlugin {
	public static final String ID = "org.vclipse.sap.deployment"
	protected static DeploymentPlugin plugin
	protected Injector injector
	protected DeploymentModule injectionModule

	override void start(BundleContext context) throws Exception {
		super.start(context)
		plugin = this
		injectionModule = new DeploymentModule(this)
	}

	override void stop(BundleContext context) throws Exception {
		plugin = null
		super.stop(context)
	}

	def static DeploymentPlugin getDefault() {
		return plugin
	}

	def static Image getImage(String key) {
		return getDefault().getImageRegistry().get(key)
	}

	def Injector getInjector(AbstractGenericModule optionalModule) {
		if (injector === null) {
			if (optionalModule !== null) {
				injector = Guice::createInjector(optionalModule)
			} else if (injectionModule !== null) {
				injector = Guice::createInjector(injectionModule)
			} else {
				throw new IllegalArgumentException("Injection module not initialized.")
			}
		}
		return injector
	}

	def Injector getInjector() {
		return getInjector(injectionModule)
	}

	def static void showErrorDialog(String dialogTitle, String message, IStatus status) {
		showErrorDialog(null, dialogTitle, message, status)
	}

	def static void showErrorDialog(Exception exception, String dialogTitle, String message) {
		showErrorDialog(exception, dialogTitle, message, null)
	}

	def static void showErrorDialog(Exception exception, String dialogTitle, String message, IStatus status) {
		log(message, null)
		val Display display = Display::getDefault()
		display.syncExec((
			[|new ErrorDialog(display.getActiveShell(), dialogTitle, message, status).open()] as Runnable))
	}

	def static void log(String message, int severity) {
		log(message, severity, null)
	}

	def static void log(String message, Throwable throwable) {
		log(message, IStatus::ERROR, throwable)
	}

	def static void log(String message, int severity, Throwable throwable) {
		getDefault().getLog().log(
			if(throwable === null) new Status(severity, ID, message) else new Status(severity, ID, IStatus::OK, message,
				throwable))
	}

}
