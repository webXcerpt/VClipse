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
/**
 * 
 */
package org.vclipse.vcml.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vclipse.vcml.utils.ISapConstants;

import com.google.inject.Inject;

/**
 *
 */
public final class SapDefaultSettingsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Inject
	private IPreferenceStore preferenceStore;

	public SapDefaultSettingsPreferencePage()  {
		super(GRID);
	}
	
	public void init(IWorkbench workbench) {
		setPreferenceStore(preferenceStore);
	}
	
	@Override
	protected void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
		Group sapValuesGroup = new Group(parent, SWT.NONE);
		sapValuesGroup.setText(" Default values for SAP materials and BOMs ");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;	
		sapValuesGroup.setLayoutData(gridData);
		sapValuesGroup.setLayout(new GridLayout(2, false));

		addField(createStringFieldEditor(ISapConstants.PLANT, "Plant:", sapValuesGroup));
		addField(createStringFieldEditor(ISapConstants.BOM_USAGE, "BOM usage:", sapValuesGroup));
		addField(createStringFieldEditor(ISapConstants.INDUSTRY_SECTOR, "Industry sector:", sapValuesGroup));
		addField(createStringFieldEditor(ISapConstants.TRANSPORTATION_GROUP, "Transportation group:", sapValuesGroup));
		addField(createStringFieldEditor(ISapConstants.LOADING_GROUP, "Loading group:", sapValuesGroup));
		addField(createStringFieldEditor(ISapConstants.SALES_ORGANISATION, "Sales organization:", sapValuesGroup));
		addField(createStringFieldEditor(ISapConstants.DISTRIBUTION_CHANNEL, "Distribution channel:", sapValuesGroup));
	}

	private StringFieldEditor createStringFieldEditor(String prefName, String label, Composite parent) {
		return new StringFieldEditor(prefName, label, 10, StringFieldEditor.VALIDATE_ON_KEY_STROKE, parent) {
			@Override
			protected void createControl(Composite parent) {
				GridLayout layout = new GridLayout();
				layout.numColumns = getNumberOfControls();
				layout.marginWidth = 10;
				layout.marginHeight = 5;
				layout.horizontalSpacing = HORIZONTAL_GAP;
				parent.setLayout(layout);
				doFillIntoGrid(parent, layout.numColumns);
			}
		};
	}
}
