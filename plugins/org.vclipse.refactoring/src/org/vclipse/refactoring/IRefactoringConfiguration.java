package org.vclipse.refactoring;

import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;

public interface IRefactoringConfiguration {

	public boolean initialize(IRefactoringContext context);
	
	public List<? extends EStructuralFeature> provideFeatures(IRefactoringContext context);
	
}
