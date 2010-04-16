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
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.vclipse.vcml2idoc.IVCML2IDocPreferences;
import org.vclipse.vcml2idoc.VCML2IDocPlugin;

/**
 *
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {

	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IEclipsePreferences preferences = new DefaultScope().getNode(VCML2IDocPlugin.ID);
		preferences.putBoolean(IVCML2IDocPreferences.BOMMAT, true);
		preferences.putBoolean(IVCML2IDocPreferences.CHRMAS, true);
		preferences.putBoolean(IVCML2IDocPreferences.CLFMAS, true);
		preferences.putBoolean(IVCML2IDocPreferences.CLSMAS, true);
		preferences.putBoolean(IVCML2IDocPreferences.CNPMAS, true);
		preferences.putBoolean(IVCML2IDocPreferences.DEPNET, true);
		preferences.putBoolean(IVCML2IDocPreferences.KNOMAS, true);
		preferences.putBoolean(IVCML2IDocPreferences.MATMAS, false);
		preferences.putBoolean(IVCML2IDocPreferences.UPSMAS, true);
		preferences.putBoolean(IVCML2IDocPreferences.VCUI_SAVEM, true);
		preferences.putBoolean(IVCML2IDocPreferences.VFNMAS, true);
		preferences.putBoolean(IVCML2IDocPreferences.VTAMAS, true);
		preferences.put(IVCML2IDocPreferences.UPSTYP, "ZVC_CML");
	}
	
	

}
