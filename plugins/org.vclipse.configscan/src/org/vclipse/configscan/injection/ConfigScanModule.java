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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.eclipse.xtext.ui.IImageHelper;
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.base.ui.util.IExtendedImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanReverseXmlTransformation;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.impl.ConfigScanReverseXmlTransformation;
import org.vclipse.configscan.impl.ConfigScanXmlProvider;
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.utils.TestCaseFactory;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;

import com.google.inject.Provider;

public class ConfigScanModule extends AbstractGenericModule {

	private ConfigScanPlugin plugin;
	
	public ConfigScanModule(ConfigScanPlugin plugin) {
		this.plugin = plugin;
	}

	public AbstractUIPlugin bindPlugin() {
		return plugin;
	}
	
	public IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore();
	}
	
	public Class<? extends IExtendedImageHelper> bindImageHelper() {
		return ClasspathAwareImageHelper.class;
	}
	
	public DocumentBuilder bindDocumentBuilder() throws ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}
	
	public IConnectionHandler bindConnectionHandler() {
		return VClipseConnectionPlugin.getDefault().getInjector().getInstance(IConnectionHandler.class);
	}

	public Class<? extends IConfigScanXMLProvider> bindConfigScanXmlProvider() {
		return ConfigScanXmlProvider.class;
	}
	
	public Class<? extends IImageHelper> bindIImageHelper() {
		return ClasspathAwareImageHelper.class;
	}
	
	public Class<? extends IConfigScanReverseXmlTransformation> bindIConfigScanReverseXmlTransformation() {
		return ConfigScanReverseXmlTransformation.class;
	}
	
	public Class<? extends TestCaseFactory> bindTestCaseFactory() {
		return TestCaseFactory.class;
	}
	
	public Provider<TestRun> registerTestRunProvider() {
		return new Provider<TestRun>() {
			@Override
			public TestRun get() {
				return plugin.getInjector(null).getInstance(TestRun.class);
			} 
		};
	}
}
