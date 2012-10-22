package org.vclipse.condition.ui.refactoring;

import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.vclipse.vcml.refactoring.VCMLRefactoring;

import com.google.common.collect.Sets;

public class ConditionRefactoring extends VCMLRefactoring {

	@Override
	public Set<EClass> getTopLevelTypes() {
		return Sets.newHashSet(VCML_PACKAGE.getConditionSource());
	}
}
