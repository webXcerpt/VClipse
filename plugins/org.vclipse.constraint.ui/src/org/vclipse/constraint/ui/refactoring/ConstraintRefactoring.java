package org.vclipse.constraint.ui.refactoring;

import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.vclipse.vcml.refactoring.VCMLRefactoring;

import com.google.common.collect.Sets;

public class ConstraintRefactoring extends VCMLRefactoring {

	@Override
	public Set<EClass> getTopLevelTypes() {
		return Sets.newHashSet(VCML_PACKAGE.getConstraintSource());
	}
}
