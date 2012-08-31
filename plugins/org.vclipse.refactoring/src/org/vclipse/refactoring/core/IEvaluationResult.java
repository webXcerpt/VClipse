package org.vclipse.refactoring.core;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public interface IEvaluationResult {

	public void setObject(EObject object);
	
	public EObject getObject();
	
	public void setFeature(EStructuralFeature feature);
	
	public EStructuralFeature getStructuralFeature();
	
	public void success(boolean success);
	
	public boolean success();
}
