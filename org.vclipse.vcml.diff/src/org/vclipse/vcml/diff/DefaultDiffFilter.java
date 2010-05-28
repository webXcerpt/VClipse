/**
 * 
 */
package org.vclipse.vcml.diff;

import org.eclipse.emf.compare.diff.metamodel.DifferenceKind;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreSwitch;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.vcml.vcml.VcmlPackage;

/**
 *	Default implementation for {@link IDiffFilter}.
 */
public class DefaultDiffFilter extends EcoreSwitch<Boolean> implements IDiffFilter {
	
	/**
	 * 
	 */
	private static final IPreferenceStore PREFERENCES = VcmlDiffPlugin.getDefault().getPreferenceStore();
	
	/**
	 * @see org.vclipse.vcml.diff.IDiffFilter#filter(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject, org.eclipse.emf.compare.diff.metamodel.DifferenceKind)
	 */
	@Override
	public boolean filter(final EObject object, final DifferenceKind kind) {
		if(DifferenceKind.CHANGE == kind) {
			return doSwitch(object);
		} else {
			return false;
		}
	}
	
	/**
	 * @see org.eclipse.emf.ecore.util.EcoreSwitch#caseEStructuralFeature(org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@Override
	public Boolean caseEStructuralFeature(final EStructuralFeature object) {
		if(VcmlPackage.Literals.MODEL__OBJECTS.equals(object)) {
			return true;
		} else if(VcmlPackage.Literals.SYMBOLIC_TYPE__VALUES.equals(object)) {
			return PREFERENCES.getBoolean(IFilterConstants.CHARACTERISTIC_IGNORE_VALUE_ORDER);
		} else if(VcmlPackage.Literals.CLASS__CHARACTERISTICS.equals(object)) {
			return PREFERENCES.getBoolean(IFilterConstants.CLASS_IGNORE_CHARACTERISTIC_ORDER);
		} else if(VcmlPackage.Literals.DEPENDENCY_NET__CONSTRAINTS.equals(object)) {
			return PREFERENCES.getBoolean(IFilterConstants.DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER);
		} else if(VcmlPackage.Literals.MATERIAL__BILLOFMATERIALS.equals(object)) {
			return PREFERENCES.getBoolean(IFilterConstants.MATERIAL_IGNORE_BOMS_ORDER);
		} else if(VcmlPackage.Literals.MATERIAL__CLASSES.equals(object)) {
			return PREFERENCES.getBoolean(IFilterConstants.MATERIAL_IGNORE_CLASSES_ORDER);
		} else if(VcmlPackage.Literals.MATERIAL__CONFIGURATIONPROFILES.equals(object)) {
			return PREFERENCES.getBoolean(IFilterConstants.MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER);
		} else if(VcmlPackage.Literals.VARIANT_FUNCTION__ARGUMENTS.equals(object)) {
			return PREFERENCES.getBoolean(IFilterConstants.VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER);
		} else if(VcmlPackage.Literals.VARIANT_TABLE__ARGUMENTS.equals(object)) {
			return PREFERENCES.getBoolean(IFilterConstants.VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER);
		} else {
			return true;
		}
	}
}
