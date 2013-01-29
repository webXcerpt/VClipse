/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.refactoring;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.vclipse.refactoring.guice.RefactoringModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class RefactoringPlugin extends AbstractUIPlugin {

	public static final String ID = "org.vclipse.refactoring";

	private static RefactoringPlugin plugin;
	
	private Injector injector;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public static RefactoringPlugin getInstance() {
		return plugin;
	}
	
	/**
	 * 
	 */
	public Injector getInjector() {
		if(injector == null) {
			RefactoringModule module = new RefactoringModule(this);
			injector = Guice.createInjector(module);
		}
		return injector;
	}
	
	/**
	 * Log a message with a given status in the error log view.
	 */
	public static void log(int status, String message) {
		getInstance().getLog().log(new Status(status, ID, message));
	}

	/**
	 * Log a predefined status.
	 */
	public static void log(RefactoringStatus status) {
		log(status.getSeverity(), status.getMessage());
	}
}
