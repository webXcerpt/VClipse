/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.vcml.ui.IUiConstants;
import org.vclipse.vcml.utils.ISapConstants;
import org.vclipse.vcml.vcml.Language;

import com.google.inject.Inject;

/**
 *	Default implementation for VClipse plug-in.
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {
	
	@Inject
	private IPreferenceStore preferenceStore;
	
	@Override
	public void initializeDefaultPreferences() {
		preferenceStore.setDefault(ISapConstants.DEFAULT_LANGUAGE, Language.EN.name());
		preferenceStore.setDefault(ISapConstants.PLANT, "1000");
		preferenceStore.setDefault(ISapConstants.BOM_USAGE, "3");
		preferenceStore.setDefault(ISapConstants.INDUSTRY_SECTOR, "M");
		preferenceStore.setDefault(ISapConstants.TRANSPORTATION_GROUP, "0001");
		preferenceStore.setDefault(ISapConstants.LOADING_GROUP, "0001");
		preferenceStore.setDefault(ISapConstants.SALES_ORGANISATION, "0001");
		preferenceStore.setDefault(ISapConstants.DISTRIBUTION_CHANNEL, "01");
		preferenceStore.setDefault(ISapConstants.PP_LINE_LENGTH, 70);
		preferenceStore.setDefault(ISapConstants.USE_PRETTY_PRINTER, true);
		
		preferenceStore.setDefault(IUiConstants.SAP_HIERARCHY_ACTIVATED, false);
		preferenceStore.setDefault(IUiConstants.OUTPUT_TO_FILE, true);
		preferenceStore.setDefault(IUiConstants.OVERWRITE, true);
	}
}
