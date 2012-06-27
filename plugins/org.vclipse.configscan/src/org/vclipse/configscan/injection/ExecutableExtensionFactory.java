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
package org.vclipse.configscan.injection;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.vclipse.configscan.ConfigScanPlugin;

import com.google.inject.Injector;

public class ExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return ConfigScanPlugin.getDefault().getBundle();
	}

	@Override
	protected Injector getInjector() {
		return ConfigScanPlugin.getDefault().getInjector(null);
	}

}
