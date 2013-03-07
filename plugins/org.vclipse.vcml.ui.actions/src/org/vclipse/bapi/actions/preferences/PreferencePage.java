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
package org.vclipse.bapi.actions.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vclipse.bapi.actions.BAPIActionPlugin;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * 
 */
public final class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	private final IPreferenceStore preferenceStore;
	
	/**
	 * @param preferenceStore
	 */
	@Inject
	public PreferencePage(@Named(BAPIActionPlugin.ID) IPreferenceStore preferenceStore) {
		super(GRID);
		this.preferenceStore = preferenceStore;
	}
	
	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {
		setPreferenceStore(preferenceStore);
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceNames.CHR_LONGTEXTS, "Extract long texts of characteristics", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceNames.CHR_DEPENDENCIES, "Extract dependencies assigned to characteristics", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceNames.CHR_VALUE_LONGTEXTS, "Extract long texts of characteristic values", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceNames.CHR_VALUE_DEPENDENCIES, "Extract dependencies assigned to characteristic values", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceNames.CLASSNODES_MATERIALS, "Extract materials assigned to class nodes", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceNames.MAT_CLASSTYPES, "Extracted class types of materials: ", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceNames.DEP_OBJECTS, "Extract objects used in source code of dependencies", getFieldEditorParent()));
	}
}
