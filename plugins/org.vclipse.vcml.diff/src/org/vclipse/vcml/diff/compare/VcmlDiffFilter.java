/**
 * 
 */
package org.vclipse.vcml.diff.compare;

import org.eclipse.emf.compare.diff.metamodel.DifferenceKind;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreSwitch;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.vcml.diff.IVcmlDiffFilter;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.inject.Inject;

public class VcmlDiffFilter extends EcoreSwitch<Boolean> implements IVcmlDiffFilter {

	private IPreferenceStore preferenceStore;
	
	@Inject
	public VcmlDiffFilter(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}
	
	public boolean changeAllowed(EObject newStateContainer, EObject oldStateContainer, EObject newStateObject, EObject oldStateObject, DifferenceKind changeKind) {
		if(oldStateContainer == null && oldStateObject == null) {
			return false;
//			newStateContainer = newStateObject.eContainer();
//			EStructuralFeature containingFeature = newStateObject.eContainmentFeature();
//			Object eGet = newStateContainer.eGet(containingFeature);
//			if(eGet instanceof EObject) {
//				EObject eo = ((EObject)eGet);
//				System.out.println(eo);
//			}
//			//attribute update
//			return !(VcmlPackage.eINSTANCE.getCharacteristicType_NumberOfChars() == newStateObject);
		} else {
			// type change of a property
			if(newStateContainer.eClass() == oldStateContainer.eClass()) {
				if(newStateObject.eClass() == oldStateObject.eClass()) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean canHandle(EObject object, DifferenceKind kind) {
		return DifferenceKind.CHANGE == kind ? doSwitch(object) : false;
	}
	
	@Override
	public Boolean caseEStructuralFeature(EStructuralFeature object) {
		if(VcmlPackage.Literals.MODEL__OBJECTS.equals(object)) {
			return true;
		} else if(VcmlPackage.Literals.SYMBOLIC_TYPE__VALUES.equals(object)) {
			return preferenceStore.getBoolean(IVcmlDiffFilter.CHARACTERISTIC_IGNORE_VALUE_ORDER);
		} else if(VcmlPackage.Literals.CLASS__CHARACTERISTICS.equals(object)) {
			return preferenceStore.getBoolean(IVcmlDiffFilter.CLASS_IGNORE_CHARACTERISTIC_ORDER);
		} else if(VcmlPackage.Literals.DEPENDENCY_NET__CONSTRAINTS.equals(object)) {
			return preferenceStore.getBoolean(IVcmlDiffFilter.DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER);
		} else if(VcmlPackage.Literals.MATERIAL__BILLOFMATERIALS.equals(object)) {
			return preferenceStore.getBoolean(IVcmlDiffFilter.MATERIAL_IGNORE_BOMS_ORDER);
		} else if(VcmlPackage.Literals.MATERIAL__CLASSIFICATIONS.equals(object)) {
			return preferenceStore.getBoolean(IVcmlDiffFilter.MATERIAL_IGNORE_CLASSES_ORDER);
		} else if(VcmlPackage.Literals.MATERIAL__CONFIGURATIONPROFILES.equals(object)) {
			return preferenceStore.getBoolean(IVcmlDiffFilter.MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER);
		} else if(VcmlPackage.Literals.VARIANT_FUNCTION__ARGUMENTS.equals(object)) {
			return preferenceStore.getBoolean(IVcmlDiffFilter.VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER);
		} else if(VcmlPackage.Literals.VARIANT_TABLE__ARGUMENTS.equals(object)) {
			return preferenceStore.getBoolean(IVcmlDiffFilter.VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER);
		} else {
			return false;
		}
	}
}
