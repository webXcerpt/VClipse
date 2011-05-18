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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.vcml2idoc.IVCML2IDocPreferences;

import com.google.inject.Inject;

/**
 *
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {

	/**
	 * 
	 */
	private final IPreferenceStore preferenceStore;
	
	/**
	 * @param preferenceStore
	 */
	@Inject
	public PreferencesInitializer(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}
	
	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
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
		preferenceStore.setDefault(IVCML2IDocPreferences.UPSTYP, "ZVC_CML");
	}
}
