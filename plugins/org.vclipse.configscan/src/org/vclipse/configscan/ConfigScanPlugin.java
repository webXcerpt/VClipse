package org.vclipse.configscan;
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

import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.base.ui.BaseUiPlugin;
import org.vclipse.configscan.injection.ConfigScanModule;

import com.google.inject.Injector;

public class ConfigScanPlugin extends BaseUiPlugin {

	static {
		ID = "org.vclipse.configscan";
	}

	@Override
	public Injector getInjector(AbstractGenericModule optionalModule) {
		return super.getInjector(optionalModule == null ? new ConfigScanModule(this) : optionalModule);
	}
}
