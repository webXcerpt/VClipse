package org.vclipse.vcml.naming;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.validation.NamesAreUniqueValidationHelper;

public class VcmlNamesAreUniqueValidationHelper extends NamesAreUniqueValidationHelper {

	@Override
	protected EClass getAssociatedClusterType(EClass eClass) {
		return eClass;
	}
}
