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
package org.vclipse.vcml2idoc.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vclipse.vcml.CombinedPreferenceStore;
import org.vclipse.vcml2idoc.IVCML2IDocPreferences;
import org.vclipse.vcml2idoc.ui.VCML2IDocUIPlugin;

/**
 *
 */
public class VCML2IDocOptionsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public VCML2IDocOptionsPreferencePage() {
		super(GRID);
		setPreferenceStore(VCML2IDocUIPlugin.getDefault().getPreferenceStore());
	}
	
	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		final Composite idocGroup = getFieldEditorParent();
		addField(new BooleanFieldEditor(IVCML2IDocPreferences.BOMMAT, "BOMMAT (Material BOM)", idocGroup));
		addField(new BooleanFieldEditor(IVCML2IDocPreferences.CHRMAS, "CHRMAS (Characteristics with dependencies and long texts)", idocGroup));
		addField(new BooleanFieldEditor(IVCML2IDocPreferences.CLFMAS, "CLFMAS (Master object classification)", idocGroup));
		addField(new BooleanFieldEditor(IVCML2IDocPreferences.CLSMAS, "CLSMAS (Master class)", idocGroup));
		addField(new BooleanFieldEditor(IVCML2IDocPreferences.CNPMAS, "CNPMAS (Master configuration profile)", idocGroup));
		addField(new BooleanFieldEditor(IVCML2IDocPreferences.DEPNET, "DEPNET (Master data of dependency net)", idocGroup));
		addField(new BooleanFieldEditor(IVCML2IDocPreferences.KNOMAS, "KNOMAS (Master dependency basic data)", idocGroup));
		addField(new DisabledBooleanFieldEditor(IVCML2IDocPreferences.MATMAS, "MATMAS (Material master)", idocGroup));
		addField(new BooleanFieldEditor(IVCML2IDocPreferences.UPSMAS, "UPSMAS (master: ALE distribution unit)", idocGroup));
		addField(new BooleanFieldEditor(IVCML2IDocPreferences.VCUI_SAVEM, "VCUI_SAVEM (Create or change interface design)", idocGroup));
		addField(new BooleanFieldEditor(IVCML2IDocPreferences.VFNMAS, "VFNMAS (Master variant function)", idocGroup));
		addField(new BooleanFieldEditor(IVCML2IDocPreferences.VTAMAS, "VTAMAS (Master variant table)", idocGroup));
		addField(new StringFieldEditor(IVCML2IDocPreferences.UPSTYP, "UPS type:", idocGroup));
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {
		// not used
	}
	
	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		super.performOk();
		((CombinedPreferenceStore)getPreferenceStore()).storePreferences();
		return true;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	@Override
	protected void performApply() {
		super.performApply();
		((CombinedPreferenceStore)getPreferenceStore()).storePreferences();
	}
}
