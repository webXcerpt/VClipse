/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *		webXcerpt Software GmbH - initial creator
 *		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.vcml.compare

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer
import org.eclipse.jface.preference.IPreferenceStore
import com.google.inject.Inject

/*
 * Default preferences initialization.
 */
class VCMLComparePreferencesInitializer extends AbstractPreferenceInitializer {
	
	@Inject
	private IPreferenceStore preferenceStore
	
	/**
	 * 
	 */
	override initializeDefaultPreferences() {
		preferenceStore.setDefault(FeatureFilter::CHARACTERISTIC_IGNORE_VALUE_ORDER, false);
		preferenceStore.setDefault(FeatureFilter::CLASS_IGNORE_CHARACTERISTIC_ORDER, true);
		preferenceStore.setDefault(FeatureFilter::DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER, true);
		preferenceStore.setDefault(FeatureFilter::MATERIAL_IGNORE_BOMS_ORDER, true);
		preferenceStore.setDefault(FeatureFilter::MATERIAL_IGNORE_CLASSES_ORDER, true);
		preferenceStore.setDefault(FeatureFilter::MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER, true);
		preferenceStore.setDefault(FeatureFilter::VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER, true);
		preferenceStore.setDefault(FeatureFilter::VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER, true);
	}
}