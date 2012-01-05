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

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanRemoteConnections;
import org.vclipse.configscan.IConfigScanRunner;
import org.vclipse.configscan.MockConfigScanRemoteConnections;
import org.vclipse.configscan.MockConfigScanRunner;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.configscan.views.ViewLabelProvider;

public class ConfigScanModule extends AbstractGenericModule {

	public IConnectionHandler bindConnectionHandler() {
		return VClipseConnectionPlugin.getDefault().getInjector().getInstance(IConnectionHandler.class);
	}
	
	public Class<? extends IConfigScanRemoteConnections> bindConfigScanRemoteConnections() {
		return MockConfigScanRemoteConnections.class;
	}
	
	public Class<? extends IConfigScanRunner> bindConfigScanRunner() {
		return MockConfigScanRunner.class;
	}
	
	public Class<? extends ILabelProvider> bindILabelProvider() {
		return ViewLabelProvider.class;
	}
	
	public ILog bindLogInstance() {
		return Platform.getLog(ConfigScanPlugin.getDefault().getBundle());
	}
}
