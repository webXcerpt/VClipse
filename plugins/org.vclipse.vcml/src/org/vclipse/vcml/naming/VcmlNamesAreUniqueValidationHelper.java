package org.vclipse.vcml.naming;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.validation.NamesAreUniqueValidationHelper;
import org.vclipse.vcml.vcml.VcmlPackage;

public class VcmlNamesAreUniqueValidationHelper extends NamesAreUniqueValidationHelper {

	@Override
	protected EClass getAssociatedClusterType(EClass eClass) {
		if(eClass == VcmlPackage.eINSTANCE.getCharacteristicValue()) {
			return null;
		}
		return eClass;
	}
}
