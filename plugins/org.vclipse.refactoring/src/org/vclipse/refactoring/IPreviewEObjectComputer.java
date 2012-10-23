package org.vclipse.refactoring;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.change.FeatureChange;

public interface IPreviewEObjectComputer {

	public Set<EClass> getFavoredTypes();
	
	public Set<EClass> getIgnoreTypes();
	
	public List<EObject> getExisting(EObject existing, EObject refactored, FeatureChange featureChange);
	
	public List<EObject> getRefactored(EObject existing, EObject refactored, FeatureChange featureChange);
	
}
