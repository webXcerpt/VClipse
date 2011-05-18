/*******************************************************************************
 * Copyright (c) 2009 webXcerpt Software GmbH (http://www.webxcerpt.com).
 * All rights reserved.
 *******************************************************************************/
package org.vclipse.idoc2jcoidoc.internal;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vclipse.idoc2jcoidoc.IUiConstants;

import com.google.inject.Inject;

/**
 * 
 */
public final class IDoc2JCoIDocPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	private final IPreferenceStore preferenceStore;
	
	/**
	 * @param preferenceStore
	 */
	@Inject
	public IDoc2JCoIDocPreferencePage(IPreferenceStore preferenceStore) {
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
		addField(new StringFieldEditor(IUiConstants.PARTNER_TYPE, "Partner type: ", getFieldEditorParent()));
		addField(new StringFieldEditor(IUiConstants.PARTNER_NUMBER, "Partner number: ", getFieldEditorParent()));
		addField(new StringFieldEditor(IUiConstants.RFC_FOR_UPS_NUMBERS, "RFC to retrieve new UPS numbers: ", getFieldEditorParent()));
		addField(new StringFieldEditor(IUiConstants.RFC_FOR_IDOC_NUMBERS, "RFC to retrieve new IDoc numbers: ", getFieldEditorParent()));
	}
}
