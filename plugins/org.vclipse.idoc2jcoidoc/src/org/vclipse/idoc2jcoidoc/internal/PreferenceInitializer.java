/*******************************************************************************
 * Copyright (c) 2009 webXcerpt Software GmbH (http://www.webxcerpt.com).
 * All rights reserved.
 *******************************************************************************/
package org.vclipse.idoc2jcoidoc.internal;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.IUiConstants;

import com.google.inject.Inject;

/**
 * 
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * Defaults
	 */
	private static final String DEFAULT_PARTNER_TYPE = "LS";
	private static final String DEFAULT_PARTNER_NUMBER = "CML";
	
	@Inject
	private IPreferenceStore preferenceStore;
	
	private Properties properties;
	
	public PreferenceInitializer() throws IOException {
		properties = new Properties();
		try {
			properties.load(FileLocator.openStream(IDoc2JCoIDocPlugin.getDefault().getBundle(), new Path("src/org/vclipse/idoc2jcoidoc/internal/rfc_overridden_settings.properties"), false));
		} catch(Exception exception) {
			properties.load(FileLocator.openStream(IDoc2JCoIDocPlugin.getDefault().getBundle(), new Path("src/org/vclipse/idoc2jcoidoc/internal/rfc_default_settings.properties"), false));		
		}
	}
	
	@Override
	public void initializeDefaultPreferences() {
		preferenceStore.putValue(IUiConstants.NUMBERS_PROVIDER, IUiConstants.TARGET_SYSTEM);
		preferenceStore.putValue(IUiConstants.RFC_FOR_UPS_NUMBERS, properties.getProperty(IUiConstants.RFC_FOR_UPS_NUMBERS));
		preferenceStore.putValue(IUiConstants.RFC_FOR_IDOC_NUMBERS, properties.getProperty(IUiConstants.RFC_FOR_IDOC_NUMBERS));
		preferenceStore.putValue(IUiConstants.PARTNER_NUMBER, DEFAULT_PARTNER_NUMBER);
		preferenceStore.putValue(IUiConstants.PARTNER_TYPE, DEFAULT_PARTNER_TYPE);
	}
}
