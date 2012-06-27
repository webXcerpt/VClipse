/**
 * 
 */
package org.vclipse.vcml.diff.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vclipse.vcml.diff.IVcmlDiffFilter;

import com.google.inject.Inject;

public class DiffPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private IPreferenceStore preferenceStore;
	
	@Inject
	public DiffPreferencePage(IPreferenceStore preferenceStore) {
		super(GRID);
		this.preferenceStore = preferenceStore;
	}
	
	@Override
	public void init(final IWorkbench workbench) {
		setDescription("Following preferences affect the model that is exported during the VCML Diff operation.");
		setPreferenceStore(preferenceStore);
	}
	
	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();
		addField(new BooleanFieldEditor(IVcmlDiffFilter.CHARACTERISTIC_IGNORE_VALUE_ORDER, 
				"Ignore values order in characteristics", parent));
		addField(new BooleanFieldEditor(IVcmlDiffFilter.CLASS_IGNORE_CHARACTERISTIC_ORDER, 
				"Ignore characteristics order in classes", parent));
		addField(new BooleanFieldEditor(IVcmlDiffFilter.DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER, 
				"Ignore constraints order in dependency nets", parent));
		addField(new BooleanFieldEditor(IVcmlDiffFilter.MATERIAL_IGNORE_BOMS_ORDER, 
				"Ignore bill of materials order in materials", parent));
		addField(new BooleanFieldEditor(IVcmlDiffFilter.MATERIAL_IGNORE_CLASSES_ORDER, 
				"Ignore classes order in materials", parent));
		addField(new BooleanFieldEditor(IVcmlDiffFilter.MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER, 
				"Ignore configuration profile order in materials", parent));
		addField(new BooleanFieldEditor(IVcmlDiffFilter.VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER, 
				"Ignore arguments order in variant functions", parent));
		addField(new BooleanFieldEditor(IVcmlDiffFilter.VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER, 
				"Ignore arguments order in variant tables", parent));
	}
}
