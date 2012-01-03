/**
 * 
 */
package org.vclipse.vcml.diff.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vclipse.vcml.diff.IDiffFilter;
import org.vclipse.vcml.diff.VcmlDiffPlugin;

public class DiffPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public DiffPreferencePage() {
		super(GRID);
	}
	
	@Override
	public void init(final IWorkbench workbench) {
		setDescription("Following preferences affect the model that is exported during the VCML Diff operation.");
		setPreferenceStore(VcmlDiffPlugin.getDefault().getPreferenceStore());
	}
	
	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();
		addField(new BooleanFieldEditor(IDiffFilter.CHARACTERISTIC_IGNORE_VALUE_ORDER, 
				"Ignore values order in characteristics", parent));
		addField(new BooleanFieldEditor(IDiffFilter.CLASS_IGNORE_CHARACTERISTIC_ORDER, 
				"Ignore characteristics order in classes", parent));
		addField(new BooleanFieldEditor(IDiffFilter.DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER, 
				"Ignore constraints order in dependency nets", parent));
		addField(new BooleanFieldEditor(IDiffFilter.MATERIAL_IGNORE_BOMS_ORDER, 
				"Ignore bill of materials order in materials", parent));
		addField(new BooleanFieldEditor(IDiffFilter.MATERIAL_IGNORE_CLASSES_ORDER, 
				"Ignore classes order in materials", parent));
		addField(new BooleanFieldEditor(IDiffFilter.MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER, 
				"Ignore configuration profile order in materials", parent));
		addField(new BooleanFieldEditor(IDiffFilter.VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER, 
				"Ignore arguments order in variant functions", parent));
		addField(new BooleanFieldEditor(IDiffFilter.VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER, 
				"Ignore arguments order in variant tables", parent));
	}
}
