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
		store.setDefault(IConfigScanConfiguration.EXPORT_XML_INPUT_TO_DISK, true);
		store.setDefault(IConfigScanConfiguration.SAVE_HISTORY, true);
		store.setDefault(IConfigScanConfiguration.HISTORY_ENTRIES_NUMBER, 10);
	}

}
