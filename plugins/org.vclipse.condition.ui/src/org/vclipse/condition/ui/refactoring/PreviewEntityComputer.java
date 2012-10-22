package org.vclipse.condition.ui.refactoring;

import java.util.Set;

import org.eclipse.emf.ecore.EClass;

public class PreviewEntityComputer extends org.vclipse.vcml.ui.refactoring.PreviewEntityComputer {

	@Override
	public Set<EClass> getTypes() {
		Set<EClass> types = super.getTypes();
		types.add(VCML_PACKAGE.getConditionSource());
		return types;
	}
}
