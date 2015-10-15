/** 
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.idoc2jcoidoc.internal

import java.io.IOException
import java.util.Properties
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer
import org.eclipse.jface.preference.IPreferenceStore
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin
import org.vclipse.idoc2jcoidoc.IUiConstants
import com.google.inject.Inject
import com.google.inject.name.Named

/** 
 */
class PreferenceInitializer extends AbstractPreferenceInitializer {
	/** 
	 * Defaults
	 */
	static final String DEFAULT_PARTNER_TYPE = "LS"
	static final String DEFAULT_PARTNER_NUMBER = "CML"
	@Inject @Named(IDoc2JCoIDocPlugin.ID) IPreferenceStore preferenceStore
	Properties properties

	new() throws IOException {
		properties = new Properties()
		var ClassLoader classLoader = getClass().getClassLoader()
		try {
			properties.load(
				classLoader.getResourceAsStream("org/vclipse/idoc2jcoidoc/internal/rfc_overridden_settings.properties"))
		} catch (Exception exception) {
			properties.load(
				classLoader.getResourceAsStream("org/vclipse/idoc2jcoidoc/internal/rfc_default_settings.properties"))
		}

	}

	override void initializeDefaultPreferences() {
		preferenceStore.putValue(IUiConstants.NUMBERS_PROVIDER, IUiConstants.TARGET_SYSTEM)
		preferenceStore.putValue(IUiConstants.RFC_FOR_UPS_NUMBERS,
			properties.getProperty(IUiConstants.RFC_FOR_UPS_NUMBERS))
		preferenceStore.putValue(IUiConstants.RFC_FOR_IDOC_NUMBERS,
			properties.getProperty(IUiConstants.RFC_FOR_IDOC_NUMBERS))
		preferenceStore.putValue(IUiConstants.PARTNER_NUMBER, DEFAULT_PARTNER_NUMBER)
		preferenceStore.putValue(IUiConstants.PARTNER_TYPE, DEFAULT_PARTNER_TYPE)
	}

}
