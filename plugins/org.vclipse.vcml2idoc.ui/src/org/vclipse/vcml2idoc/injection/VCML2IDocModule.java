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
package org.vclipse.vcml2idoc.injection;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.vcml.ui.VCMLUiPlugin;
import org.vclipse.vcml2idoc.VCML2IDocPlugin;
import org.vclipse.vcml2idoc.transformation.IVCML2IDocTransformation;
import org.vclipse.vcml2idoc.transformation.VCML2IDocSwitch;
import org.vclipse.vcml2idoc.transformation.VCML2IDocTransformation;

import com.google.inject.Binder;
import com.google.inject.name.Names;

public class VCML2IDocModule extends AbstractGenericModule {

	private AbstractUIPlugin plugin;
	
	public VCML2IDocModule(AbstractUIPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void configure(Binder binder) {
		super.configure(binder);
		IPreferenceStore vcmlUiPreferenceStore = VCMLUiPlugin.getInjector().getInstance(IPreferenceStore.class);
		binder.bind(IPreferenceStore.class).annotatedWith(Names.named(VCMLUiPlugin.ID)).toInstance(vcmlUiPreferenceStore);
        binder.bind(IPreferenceStore.class).annotatedWith(Names.named(VCML2IDocPlugin.ID)).toInstance(plugin.getPreferenceStore());
	}

	public IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore();
	}
	
	public Class<? extends IVCML2IDocTransformation> bindVcml2IDocTransformation() {
		return VCML2IDocTransformation.class;
	}
	
	public Class<? extends VCML2IDocSwitch> bindVcml2IDocSwitch() {
		return VCML2IDocSwitch.class;
	}
}
