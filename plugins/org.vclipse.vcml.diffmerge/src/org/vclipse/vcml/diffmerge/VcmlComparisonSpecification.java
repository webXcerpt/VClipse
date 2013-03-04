package org.vclipse.vcml.diffmerge;

import org.eclipse.emf.diffmerge.api.IMatchPolicy;
import org.eclipse.emf.diffmerge.ui.specification.IScopeSpecification;
import org.eclipse.emf.diffmerge.ui.specification.ext.DefaultComparisonSpecification;

public class VcmlComparisonSpecification extends DefaultComparisonSpecification {

	public VcmlComparisonSpecification(IScopeSpecification leftScopeSpec_p,
			IScopeSpecification rightScopeSpec_p,
			IScopeSpecification ancestorScopeSpec_p) {
		super(leftScopeSpec_p, rightScopeSpec_p, ancestorScopeSpec_p);
	}

	@Override
	protected IMatchPolicy createMatchPolicy() {
		return new VcmlMatchPolicy();
	}
	
}
