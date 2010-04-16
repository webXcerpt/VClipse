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
package org.vclipse.vcml.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.vclipse.vcml.IUiConstants;
import org.vclipse.vcml.VCMLUiPlugin;


/**
 * 
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {
	
	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IEclipsePreferences preferences = new DefaultScope().getNode(VCMLUiPlugin.ID);
		preferences.putBoolean(IUiConstants.SAP_HIERARCHY_ACTIVATED, false);
		preferences.putBoolean(IUiConstants.OUTPUT_TO_FILE, true);
		preferences.putBoolean(IUiConstants.OVERWRITE, true);
	}
}
