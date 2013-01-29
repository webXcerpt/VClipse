package org.vclipse.vcml.compare.ui

import com.google.inject.Inject
import org.eclipse.jface.preference.FieldEditorPreferencePage
import org.eclipse.jface.preference.IPreferenceStore
import org.eclipse.ui.IWorkbench
import org.eclipse.ui.IWorkbenchPreferencePage

import static org.eclipse.jface.preference.FieldEditorPreferencePage.*
import org.vclipse.vcml.compare.FeatureFilter
import org.eclipse.jface.preference.BooleanFieldEditor

/*
 * Preference page allowing the user to match the values that should be ignored during compare operation.
 */
class VcmlComparePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private IPreferenceStore preferenceStore

	@Inject
	new(IPreferenceStore preferenceStore) {
		super(GRID)
		this.preferenceStore = preferenceStore
	}
	
	/*
	 * sets the description and the preference store
	 */
	override init(IWorkbench workbench) {
		setDescription("Following preferences affect the model that is exported during the VCML Diff operation.")
		setPreferenceStore(preferenceStore)
	}

	/*
	 * Creates boolean field editors for different values
	 */
	override protected createFieldEditors() {
		addBooleanFieldEditor(FeatureFilter::CHARACTERISTIC_IGNORE_VALUE_ORDER, "Ignore values order in characteristics")
		addBooleanFieldEditor(FeatureFilter::CLASS_IGNORE_CHARACTERISTIC_ORDER, "Ignore characteristics order in classes")
		addBooleanFieldEditor(FeatureFilter::DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER, "Ignore constraints order in dependency nets")
		addBooleanFieldEditor(FeatureFilter::MATERIAL_IGNORE_BOMS_ORDER, "Ignore bill of materials order in materials")
		addBooleanFieldEditor(FeatureFilter::MATERIAL_IGNORE_CLASSES_ORDER, "Ignore classes order in materials")
		addBooleanFieldEditor(FeatureFilter::MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER, "Ignore configuration profile order in materials")
		addBooleanFieldEditor(FeatureFilter::VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER, "Ignore arguments order in variant functions")
		addBooleanFieldEditor(FeatureFilter::VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER, "Ignore arguments order in variant tables")
	}
	
	/*
	 * Creates a boolean field editor.
	 */
	def protected addBooleanFieldEditor(String name, String label) {
		val parent = getFieldEditorParent()
		addField(new BooleanFieldEditor(name, label, parent))
	}
}