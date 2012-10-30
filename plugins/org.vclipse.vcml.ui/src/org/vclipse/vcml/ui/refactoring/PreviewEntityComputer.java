package org.vclipse.vcml.ui.refactoring;

import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.vclipse.refactoring.core.ContainerPreviewComputer;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Sets;

public class PreviewEntityComputer extends ContainerPreviewComputer {

	protected static final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;

	@Override
	public Set<EClass> getFavoredTypes() {
		return Sets.newHashSet(VCML_PACKAGE.getVCObject(), VCML_PACKAGE.getCondition(), VCML_PACKAGE.getConstraint(), VCML_PACKAGE.getProcedure());
	}

	@Override
	public Set<EClass> getIgnoreTypes() {
		return Sets.newHashSet(VCML_PACKAGE.getVcmlModel(), VCML_PACKAGE.getConstraintSource(), VCML_PACKAGE.getProcedureSource(), VCML_PACKAGE.getConditionSource());
	}
}
