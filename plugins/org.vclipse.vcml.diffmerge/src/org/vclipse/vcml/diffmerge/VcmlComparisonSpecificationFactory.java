package org.vclipse.vcml.diffmerge;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.diffmerge.ui.specification.IComparisonSpecification;
import org.eclipse.emf.diffmerge.ui.specification.IScopeSpecification;
import org.eclipse.emf.diffmerge.ui.specification.ext.DefaultComparisonSpecificationFactory;

public class VcmlComparisonSpecificationFactory extends
		DefaultComparisonSpecificationFactory {

	public VcmlComparisonSpecificationFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IComparisonSpecification createComparisonSpecification(
			IScopeSpecification leftScopeSpec_p,
			IScopeSpecification rightScopeSpec_p,
			IScopeSpecification ancestorScopeSpec_p) {
		// TODO Auto-generated method stub
		return new VcmlComparisonSpecification(leftScopeSpec_p, rightScopeSpec_p,
				ancestorScopeSpec_p);
	}

	@Override
	public String getLabel() {
		return "VCML Object Matching (by eclass/name)";
	}

	@Override
	public Collection<Class<?>> getOverridenClasses() {
		return Collections.<Class<?>>singleton(DefaultComparisonSpecificationFactory.class);
	}

}
