/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
/**
 * 
 */
package org.vclipse.vcml2idoc.preferences;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.vcml2idoc.IVCML2IDocPreferences;
import org.vclipse.vcml2idoc.VCML2IDocUIPlugin;

import com.google.inject.Inject;

/**
 *
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {

	@Inject
	private IPreferenceStore preferenceStore;
	
	private Properties properties;
	
	public PreferencesInitializer() throws IOException {
		properties = new Properties();
		try {
			properties.load(FileLocator.openStream(VCML2IDocUIPlugin.getDefault().getBundle(), new Path("src/org/vclipse/vcml2idoc/preferences/vcml2idoc_settings_overriden.properties"), false));
		} catch(Exception exception) {
			properties.load(FileLocator.openStream(VCML2IDocUIPlugin.getDefault().getBundle(), new Path("src/org/vclipse/vcml2idoc/preferences/vcml2idoc_settings_default.properties"), false));			
		}
	}
	
	@Override
	public void initializeDefaultPreferences() {
		preferenceStore.setDefault(IVCML2IDocPreferences.BOMMAT, false);
		preferenceStore.setDefault(IVCML2IDocPreferences.CHRMAS, true);
		preferenceStore.setDefault(IVCML2IDocPreferences.CLFMAS, true);
		preferenceStore.setDefault(IVCML2IDocPreferences.CLSMAS, true);
		preferenceStore.setDefault(IVCML2IDocPreferences.CNPMAS, true);
		preferenceStore.setDefault(IVCML2IDocPreferences.DEPNET, true);
		preferenceStore.setDefault(IVCML2IDocPreferences.KNOMAS, true);
		preferenceStore.setDefault(IVCML2IDocPreferences.MATMAS, false);
		preferenceStore.setDefault(IVCML2IDocPreferences.UPSMAS, true);
		preferenceStore.setDefault(IVCML2IDocPreferences.VCUI_SAVEM, true);
		preferenceStore.setDefault(IVCML2IDocPreferences.VFNMAS, false);
		preferenceStore.setDefault(IVCML2IDocPreferences.VTAMAS, true);
		preferenceStore.setDefault(IVCML2IDocPreferences.VTMMAS, true);
		preferenceStore.setDefault(IVCML2IDocPreferences.UPSTYPE, properties.getProperty(IVCML2IDocPreferences.UPSTYPE));
	}
}
