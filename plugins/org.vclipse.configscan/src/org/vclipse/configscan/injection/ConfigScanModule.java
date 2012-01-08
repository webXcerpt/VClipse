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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.configscan.IConfigScanRemoteConnections;
import org.vclipse.configscan.IConfigScanRunner;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.MockConfigScanRemoteConnections;
import org.vclipse.configscan.MockConfigScanRunner;
import org.vclipse.configscan.implementation.ConfigScanXmlProvider;
import org.vclipse.configscan.views.DefaultConfigScanLabelProvider;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;

public final class ConfigScanModule extends AbstractGenericModule {

	private final AbstractUIPlugin plugin;

	public ConfigScanModule(AbstractUIPlugin plugin) {
		this.plugin = plugin;
	}
	
	public ImageRegistry bindImageDescriptor() {
		return plugin.getImageRegistry();
	}
	
	public IPreferenceStore bindIPreferenceStore() {
		return plugin.getPreferenceStore();
	}
	
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
		return DefaultConfigScanLabelProvider.class;
	}
	
	public Class<? extends IConfigScanXMLProvider> bindConfigScanXmlProvider() {
		return ConfigScanXmlProvider.class;
	}
	
	public DocumentBuilder bindDocumentBuilder() throws ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}
}
