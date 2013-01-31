/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator and others
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan.vcmlt.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.imports.IConfigScanImportTransformation;
import org.vclipse.configscan.vcmlt.builder.VcmlTConfigScanXMLProvider;
import org.vclipse.configscan.vcmlt.imports.Cfg2VcmlTImportTransformation;
import org.vclipse.configscan.vcmlt.imports.ConfigScanXml2VcmlTImportTransformation;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used within the IDE.
 */
public class VcmlTUiModule extends org.vclipse.configscan.vcmlt.ui.AbstractVcmlTUiModule {
	
	public VcmlTUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}
	
	public void configure(Binder binder) {
		super.configure(binder);
		binder.bind(IConfigScanImportTransformation.class).annotatedWith(
				Names.named("Cfg2VcmlTImportTransformation")).to(Cfg2VcmlTImportTransformation.class);
		binder.bind(IConfigScanImportTransformation.class).annotatedWith(
				Names.named("ConfigScanXml2VcmlTImportTransformation")).to(ConfigScanXml2VcmlTImportTransformation.class);
	}
	
	public IConnectionHandler bindConnectionHandler() {
		return VClipseConnectionPlugin.getDefault().getInjector().getInstance(IConnectionHandler.class);
	}

	public Class<? extends IConfigScanXMLProvider> bindConfigScanXMLProvider() {
		return VcmlTConfigScanXMLProvider.class;
	}
	
}
