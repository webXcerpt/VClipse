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
package org.vclipse.configscan.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.configscan.IConfigScanConfiguration;

import com.google.inject.Inject;

public class PreferencesInitializer extends AbstractPreferenceInitializer {

	private final IPreferenceStore store;
	
	@Inject
	public PreferencesInitializer(IPreferenceStore preferenceStore) {
		store = preferenceStore;
	}
	
	@Override
	public void initializeDefaultPreferences() {
		store.setDefault(IConfigScanConfiguration.EXPAND_TREE_ON_INPUT, true);
		store.setDefault(IConfigScanConfiguration.SAVE_HISTORY, true);
		store.setDefault(IConfigScanConfiguration.HISTORY_ENTRIES_NUMBER, 10);
	}
}
