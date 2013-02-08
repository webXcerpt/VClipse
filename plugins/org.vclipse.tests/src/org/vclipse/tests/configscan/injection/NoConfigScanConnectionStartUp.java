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
package org.vclipse.tests.configscan.injection;

import org.eclipse.ui.IStartup;
import org.vclipse.configscan.ConfigScanPlugin;

public class NoConfigScanConnectionStartUp implements IStartup {

	@Override
	public void earlyStartup() {
		ConfigScanPlugin plugin = ConfigScanPlugin.getDefault();
		TestConfigScanModule testModule = new TestConfigScanModule(plugin);
		plugin.getInjector(testModule);
	}
}
