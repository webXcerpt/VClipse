/**
 * 
 */
package org.vclipse.vcml.diff.internal;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vclipse.vcml.diff.IFilterConstants;
import org.vclipse.vcml.diff.VcmlDiffPlugin;

/**
 *
 */
public class DiffPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public DiffPreferencePage() {
		super(GRID);
	}
	
	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {
		setDescription("Following preferences affect the model that is exported during the VCML Diff operation.");
		setPreferenceStore(VcmlDiffPlugin.getDefault().getPreferenceStore());
	}
	
	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
		addField(new BooleanFieldEditor(IFilterConstants.CHARACTERISTIC_IGNORE_VALUE_ORDER, 
				"Ignore values order in characteristics", parent));
		addField(new BooleanFieldEditor(IFilterConstants.CLASS_IGNORE_CHARACTERISTIC_ORDER, 
				"Ignore characteristics order in classes", parent));
		addField(new BooleanFieldEditor(IFilterConstants.DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER, 
				"Ignore constraints order in dependency nets", parent));
		addField(new BooleanFieldEditor(IFilterConstants.MATERIAL_IGNORE_BOMS_ORDER, 
				"Ignore bill of materials order in materials", parent));
		addField(new BooleanFieldEditor(IFilterConstants.MATERIAL_IGNORE_CLASSES_ORDER, 
				"Ignore classes order in materials", parent));
		addField(new BooleanFieldEditor(IFilterConstants.MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER, 
				"Ignore configuration profile order in materials", parent));
		addField(new BooleanFieldEditor(IFilterConstants.VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER, 
				"Ignore arguments order in variant functions", parent));
		addField(new BooleanFieldEditor(IFilterConstants.VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER, 
				"Ignore arguments order in variant tables", parent));
	}
}
