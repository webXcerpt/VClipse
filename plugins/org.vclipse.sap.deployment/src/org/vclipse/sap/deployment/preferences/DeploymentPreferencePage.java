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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.inject.Inject;

public class DeploymentPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Inject
	public DeploymentPreferencePage(IPreferenceStore preferenceStore) {
		super(GRID);
		setPreferenceStore(preferenceStore);
		setTitle("SAP Deployment Settings");
	}
	
	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferencesInitializer.SAVE_DIFF_FILES, "Save diff files", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferencesInitializer.SAVE_IDOC_FILES, "Save idoc files", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferencesInitializer.EXECUTE_SVN_COMMIT, "Commit the new SAP state", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// not used
	}
}
