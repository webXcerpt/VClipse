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
package org.vclipse.sap.deployment.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.sap.deployment.DeploymentPlugin;

import com.google.inject.Inject;

public class PreferencesInitializer extends AbstractPreferenceInitializer {

	public static final String SAVE_DIFF_FILES = DeploymentPlugin.ID + ".saveDiffFiles";
	
	public static final String SAVE_IDOC_FILES = DeploymentPlugin.ID + ".saveIDocFiles";
	
	public static final String EXECUTE_SVN_COMMIT = DeploymentPlugin.ID + ".executeSvnCommit";
	
	@Inject
	private IPreferenceStore preferenceStore;

	@Override
	public void initializeDefaultPreferences() {
		preferenceStore.setDefault(SAVE_DIFF_FILES, false);
		preferenceStore.setDefault(SAVE_IDOC_FILES, false);
		preferenceStore.setDefault(EXECUTE_SVN_COMMIT, false);
	}
}
