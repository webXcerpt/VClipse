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
package org.vclipse.configscan.injection;

import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanRemoteConnections;
import org.vclipse.configscan.IConfigScanRunner;

public class TestConfigScanModule extends ConfigScanModule {

	public TestConfigScanModule(ConfigScanPlugin plugin) {
		super(plugin);
	}

	public Class<? extends IConfigScanRemoteConnections> bindConfigScanRemoteConnections() {
		return MockConfigScanRemoteConnections.class;
	}

	public Class<? extends IConfigScanRunner> bindConfigScanRunner() {
		return MockConfigScanRunner.class;
	}
}
