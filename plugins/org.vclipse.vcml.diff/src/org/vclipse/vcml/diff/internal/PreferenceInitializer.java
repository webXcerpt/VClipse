/**
 * 
 */
package org.vclipse.vcml.diff.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.vcml.diff.IFilterConstants;
import org.vclipse.vcml.diff.VcmlDiffPlugin;

/**
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore preferences = VcmlDiffPlugin.getDefault().getPreferenceStore();
		preferences.setDefault(IFilterConstants.CHARACTERISTIC_IGNORE_VALUE_ORDER, true);
		preferences.setDefault(IFilterConstants.CLASS_IGNORE_CHARACTERISTIC_ORDER, true);
		preferences.setDefault(IFilterConstants.DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER, true);
		preferences.setDefault(IFilterConstants.MATERIAL_IGNORE_BOMS_ORDER, true);
		preferences.setDefault(IFilterConstants.MATERIAL_IGNORE_CLASSES_ORDER, true);
		preferences.setDefault(IFilterConstants.MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER, true);
		preferences.setDefault(IFilterConstants.VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER, true);
		preferences.setDefault(IFilterConstants.VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER, true);
	}
}
