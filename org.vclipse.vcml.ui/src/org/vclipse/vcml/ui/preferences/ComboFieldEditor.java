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

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.vclipse.vcml.utils.ISapConstants;

/**
 *
 */
public final class ComboFieldEditor extends FieldEditor {
	
	/**
	 *	Entries of the widget
	 */
	private Iterable<?> comboEntries;
	
	/**
	 *	Combo widget for the LanguageFieldEditor
	 */
	private Combo combo;
	
	/**
	 *	Preference store
	 */
	private IPreferenceStore preferenceStore;
	
	/**
	 * @param preferenceName
	 * @param label
	 * @param parent - 
	 * @param comboEntries - a String list will be shown in the ComboBox
	 */
	public ComboFieldEditor(final IPreferenceStore preferenceStore, Composite parent, Iterable<?> entries) {
		this.preferenceStore = preferenceStore;
		setPreferenceStore(preferenceStore);
		setPreferenceName(ISapConstants.DEFAULT_LANGUAGE);
		setLabelText("Default language: ");
		comboEntries = entries;
		createControl(parent);
		combo.setText(preferenceStore.getString(ISapConstants.DEFAULT_LANGUAGE));
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#getPreferenceName()
	 */
	@Override
	public String getPreferenceName() {
		return ISapConstants.DEFAULT_LANGUAGE;
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {
		GridData gd = (GridData)combo.getLayoutData();
        gd.horizontalSpan = numColumns - 1;
        gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		getLabelControl(parent);
        combo = getComboControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns - 1;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        combo.setLayoutData(gd);
	}

	@Override
	protected void doLoad() {
		if(combo != null) {
			combo.setText(getPreferenceStore().getString(getPreferenceName()));
        }
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		if(combo != null) {
			combo.setText(preferenceStore.getDefaultString(getPreferenceName()));
        }
	}
	
	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		String language = combo.getText();
		getPreferenceStore().putValue(getPreferenceName(), language);
		fireValueChanged(getPreferenceName(), "", language);
	}


	/**
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		return 2;
	}

	/**
	 * @param parent
	 * @return
	 */
	public Combo getComboControl(Composite parent) {
		if(combo == null) {
			combo = new Combo(parent, SWT.SINGLE | SWT.DROP_DOWN | SWT.READ_ONLY);
			combo.setFont(parent.getFont());
			for(Object entry : comboEntries) {
				if(entry != null) {
					combo.add(entry.toString());
				}
			}
			combo.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                	combo = null;
                }
            });
		}
		return combo;
	}
}
