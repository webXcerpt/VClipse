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
package org.vclipse.bapi.actions.preferences;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.bapi.actions.BAPIActionPlugin;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * 
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Inject
	@Named(BAPIActionPlugin.ID)
	private IPreferenceStore preferenceStore;
	
	private Properties properties;
	
	public PreferenceInitializer() throws IOException {
		properties = new Properties();
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			properties.load(
					classLoader.getResourceAsStream(
							"org/vclipse/bapi/actions/preferences/overridden_preferences.properties"));
		} catch(Exception exception) {
			properties.load(
					classLoader.getResourceAsStream(
							"org/vclipse/bapi/actions/preferences/default_preferences.properties"));		
		}
	}
	
	@Override
	public void initializeDefaultPreferences() {
		preferenceStore.setValue(PreferenceNames.CHR_LONGTEXTS, getBooleanProperty(PreferenceNames.CHR_LONGTEXTS));
		preferenceStore.setValue(PreferenceNames.CHR_DEPENDENCIES, getBooleanProperty(PreferenceNames.CHR_DEPENDENCIES));
		preferenceStore.setValue(PreferenceNames.CHR_VALUE_LONGTEXTS, getBooleanProperty(PreferenceNames.CHR_VALUE_LONGTEXTS));
		preferenceStore.setValue(PreferenceNames.CHR_VALUE_DEPENDENCIES, getBooleanProperty(PreferenceNames.CHR_VALUE_DEPENDENCIES));
		preferenceStore.setValue(PreferenceNames.CLASSNODES_MATERIALS, getBooleanProperty(PreferenceNames.CLASSNODES_MATERIALS));
		preferenceStore.setValue(PreferenceNames.MAT_CLASSTYPES, properties.getProperty(PreferenceNames.MAT_CLASSTYPES));
		preferenceStore.setValue(PreferenceNames.DEP_OBJECTS, getBooleanProperty(PreferenceNames.DEP_OBJECTS));
	}
	
	private boolean getBooleanProperty(String property) {
		return "true".equalsIgnoreCase(properties.getProperty(property));
	}
}
