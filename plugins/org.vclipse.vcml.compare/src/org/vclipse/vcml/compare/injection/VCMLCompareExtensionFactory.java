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
package org.vclipse.vcml.compare.injection;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.vclipse.vcml.compare.VCMLComparePlugin;

import com.google.inject.Injector;

/**
 * Default implementation for Vcml Compare Plug-in.
 */
public class VCMLCompareExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return VCMLComparePlugin.getInstance().getBundle();
	}

	@Override
	protected Injector getInjector() {
		return VCMLComparePlugin.getInstance().getInjector();
	}
}
