/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.vclipse.vcml.ui.internal.VCMLActivator;

import com.google.inject.Injector;

public class VCMLUiPlugin extends VCMLActivator {

	public static final String ID = "org.vclipse.vcml.ui";

	public static Injector getInjector() {
		return getInstance().getInjector(VCMLActivator.ORG_VCLIPSE_VCML_VCML);
	}
	
	public static void log(final IStatus status) {
		getInstance().getLog().log(status);
	}

	public static void log(final String message, final Throwable thr) {
		log(new Status(IStatus.ERROR, ID, IStatus.ERROR, message, thr));		
	}
}
