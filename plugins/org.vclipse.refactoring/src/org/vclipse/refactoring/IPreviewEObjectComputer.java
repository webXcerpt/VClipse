package org.vclipse.refactoring;

import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public interface IPreviewEObjectComputer {

	public Set<EClass> getTypes();
	
	public EObject getExisting(EObject container, EObject original, EObject refactored, EStructuralFeature feature);
	
	public EObject getRefactored(EObject container, EObject original, EObject refactored, EStructuralFeature feature);
	
}
