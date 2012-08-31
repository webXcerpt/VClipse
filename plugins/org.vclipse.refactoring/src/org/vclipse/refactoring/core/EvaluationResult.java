package org.vclipse.refactoring.core;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class EvaluationResult implements IEvaluationResult {

	private EObject object;
	private EStructuralFeature feature;
	private boolean success;
	
	public static IEvaluationResult getTrueResult(EObject object) {
		return new EvaluationResult(object, null, true);
	}
	
	public static IEvaluationResult getTrueResult(EObject object, EStructuralFeature feature) {
		return new EvaluationResult(object, feature, true);
	}
	
	public static IEvaluationResult getFalseResult(EObject object) {
		return new EvaluationResult(object, null, false);
	}
	
	public static IEvaluationResult getFalseResult(EObject object, EStructuralFeature feature) {
		return new EvaluationResult(object, feature, false);
	}
	
	public EvaluationResult(EObject object, EStructuralFeature feature, boolean success) {
		this.object = object;
		this.feature = feature;
		this.success = success;
	}
	
	public EvaluationResult() {
		
	}
	
	@Override
	public void setObject(EObject object) {
		this.object = object;
	}

	@Override
	public EObject getObject() {
		return object;
	}

	@Override
	public void setFeature(EStructuralFeature feature) {
		this.feature = feature;
	}

	@Override
	public EStructuralFeature getStructuralFeature() {
		return feature;
	}

	@Override
	public void success(boolean success) {
		this.success = success;
	}

	@Override
	public boolean success() {
		return success;
	}
}
