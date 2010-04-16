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
package org.vclipse.vcml.utils;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.vclipse.vcml.VCMLPlugin;
import org.vclipse.vcml.vcml.Language;


/**
 * 
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences preferences = new DefaultScope().getNode(VCMLPlugin.ID);
		preferences.put(ISapConstants.DEFAULT_LANGUAGE, Language.EN.name());
		preferences.put(ISapConstants.PLANT, "1000");
		preferences.put(ISapConstants.BOM_USAGE, "3");
		preferences.put(ISapConstants.INDUSTRY_SECTOR, "M");
		preferences.put(ISapConstants.TRANSPORTATION_GROUP, "0001");
		preferences.put(ISapConstants.LOADING_GROUP, "0001");
		preferences.put(ISapConstants.SALES_ORGANISATION, "0001");
		preferences.put(ISapConstants.DISTRIBUTION_CHANNEL, "01");
		preferences.putInt(ISapConstants.PP_LINE_LENGTH, 70);
		preferences.putBoolean(ISapConstants.USE_PRETTY_PRINTER, true);
	}
}
