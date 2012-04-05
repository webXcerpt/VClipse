package org.vclipse.vcml.naming;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.validation.NamesAreUniqueValidationHelper;
import org.vclipse.vcml.vcml.VcmlPackage;

public class VcmlNamesAreUniqueValidationHelper extends NamesAreUniqueValidationHelper {

	private static final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;

	@Override
	protected EClass getAssociatedClusterType(EClass eClass) {
		if(eClass == VCML_PACKAGE.getCharacteristicValue() || eClass == VCML_PACKAGE.getConstraintClass()) {
			return null;
		}
		return eClass;
	}
}
