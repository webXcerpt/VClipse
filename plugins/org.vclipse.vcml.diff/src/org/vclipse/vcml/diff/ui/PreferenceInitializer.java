/**
 * 
 */
package org.vclipse.vcml.diff.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.vcml.diff.IDiffFilter;
import org.vclipse.vcml.diff.VcmlDiffPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferences = VcmlDiffPlugin.getDefault().getPreferenceStore();
		preferences.setDefault(IDiffFilter.CHARACTERISTIC_IGNORE_VALUE_ORDER, true);
		preferences.setDefault(IDiffFilter.CLASS_IGNORE_CHARACTERISTIC_ORDER, true);
		preferences.setDefault(IDiffFilter.DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER, true);
		preferences.setDefault(IDiffFilter.MATERIAL_IGNORE_BOMS_ORDER, true);
		preferences.setDefault(IDiffFilter.MATERIAL_IGNORE_CLASSES_ORDER, true);
		preferences.setDefault(IDiffFilter.MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER, true);
		preferences.setDefault(IDiffFilter.VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER, true);
		preferences.setDefault(IDiffFilter.VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER, true);
	}
}
