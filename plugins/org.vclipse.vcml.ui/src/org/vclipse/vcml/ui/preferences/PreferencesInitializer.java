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
package org.vclipse.vcml.ui.preferences;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.vcml.ui.IUiConstants;
import org.vclipse.vcml.utils.ISapConstants;
import org.vclipse.vcml.vcml.Language;

import com.google.inject.Inject;

public class PreferencesInitializer extends AbstractPreferenceInitializer {
	
	@Inject
	private IPreferenceStore preferenceStore;
	
	private Properties properties;
	
	public PreferencesInitializer() throws IOException {
		properties = new Properties();
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			properties.load(classLoader.getResourceAsStream("org/vclipse/vcml/ui/preferences/sap_overridden_settings.properties"));
		} catch(Exception exception) {
			properties.load(classLoader.getResourceAsStream("org/vclipse/vcml/ui/preferences/sap_default_settings.properties"));			
		}
	}
	
	@Override
	public void initializeDefaultPreferences() {
		preferenceStore.setDefault(ISapConstants.DEFAULT_LANGUAGE, Language.EN.name());
		preferenceStore.setDefault(ISapConstants.PLANT, properties.getProperty(ISapConstants.PLANT));
		preferenceStore.setDefault(ISapConstants.BOM_USAGE, properties.getProperty(ISapConstants.BOM_USAGE));
		preferenceStore.setDefault(ISapConstants.INDUSTRY_SECTOR, properties.getProperty(ISapConstants.INDUSTRY_SECTOR));
		preferenceStore.setDefault(ISapConstants.TRANSPORTATION_GROUP, properties.getProperty(ISapConstants.TRANSPORTATION_GROUP));
		preferenceStore.setDefault(ISapConstants.LOADING_GROUP, properties.getProperty(ISapConstants.LOADING_GROUP));
		preferenceStore.setDefault(ISapConstants.SALES_ORGANISATION, properties.getProperty(ISapConstants.SALES_ORGANISATION));
		preferenceStore.setDefault(ISapConstants.DISTRIBUTION_CHANNEL, properties.getProperty(ISapConstants.DISTRIBUTION_CHANNEL));
		preferenceStore.setDefault(ISapConstants.PP_LINE_LENGTH, 70);
		preferenceStore.setDefault(ISapConstants.USE_PRETTY_PRINTER, true);
		
		preferenceStore.setDefault(IUiConstants.SAP_HIERARCHY_ACTIVATED, false);
		preferenceStore.setDefault(IUiConstants.OUTPUT_TO_FILE, true);
		preferenceStore.setDefault(IUiConstants.OVERWRITE, true);
	}
}
