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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 *
 */
public final class ComboFieldEditor extends FieldEditor {
	
	/**
	 * 
	 */
	private Iterable<?> comboEntries;
	
	/**
	 * 
	 */
	private Combo combo;
	
	/**
	 * 
	 */
	private String prevValue;
	
	/**
	 * 
	 */
	private IPreferencesService preferencesService;
	
	/**
	 * 
	 */
	private Preferences defaultPreferences;
	
	/**
	 * 
	 */
	private Preferences actualPreferences;
	
	/**
	 * 
	 */
	private String pluginid;
	
	/**
	 * 
	 */
	private String preferenceName;
	
	/**
	 * @param preferenceName
	 * @param label
	 * @param parent - 
	 * @param comboEntries - a String list will be shown in the ComboBox
	 */
	public ComboFieldEditor(String pluginid, String preferenceName, String label, Composite parent, Iterable<?> entries) {
		this.preferenceName = preferenceName;
		init(preferenceName, label);
		comboEntries = entries;
		createControl(parent);
		this.pluginid = pluginid;
		preferencesService = Platform.getPreferencesService();
		actualPreferences = new InstanceScope().getNode(pluginid);
		defaultPreferences = new DefaultScope().getNode(pluginid);
		combo.setText(preferencesService.getString(pluginid, getPreferenceName(), "", null));
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#getPreferenceName()
	 */
	@Override
	public String getPreferenceName() {
		return preferenceName;
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

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {
		if(combo != null) {
			String value = Platform.getPreferencesService().getString(pluginid, getPreferenceName(), "", null);
            combo.setText(value);
            prevValue = value;
        }
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		prevValue = combo.getText();
		if(combo != null) {
			combo.setText(defaultPreferences.get(getPreferenceName(), " "));
        }
	}
	
	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		String currentValue = combo.getText();
		if(prevValue != currentValue) {
			actualPreferences.put(getPreferenceName(), currentValue);
			try {
				actualPreferences.flush();
				fireValueChanged(VALUE, prevValue, currentValue);
	            prevValue = currentValue;
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
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
