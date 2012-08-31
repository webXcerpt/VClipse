package org.vclipse.refactoring.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.vclipse.refactoring.RefactoringPlugin;

import com.google.common.collect.Lists;

@SuppressWarnings("unchecked")
public class RefactoringCustomisation extends MethodCollector {

	public static final String FEATURES_PREFIX = "features_";
	public static final String EVALUATION_PREFIX = "evaluate_";
	
	public RefactoringCustomisation() {
		super(2);
	}
	 
	public IEvaluationResult evaluate(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		String prefix = EVALUATION_PREFIX + context.getType();
		Pair<EObject, Method> pair = getMethod(element, context.getStructuralFeature(), prefix);
		if(pair != null) {
			try {
				return (IEvaluationResult)pair.getSecond().invoke(this, new Object[]{context, pair.getFirst()});
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				RefactoringPlugin.log(e.getMessage(), e);
			}
		} 
		return EvaluationResult.getFalseResult(element);
	}
	
	public List<? extends EStructuralFeature> features(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		String prefix = FEATURES_PREFIX + context.getType();
		Pair<EObject, Method> pair = getMethod(element, context.getStructuralFeature(), prefix);
		if(pair != null) {
			try {
				return (List<EStructuralFeature>)pair.getSecond().invoke(this, new Object[]{context, pair.getFirst()});
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				RefactoringPlugin.log(e.getMessage(), e);
			}
		} 
		return Lists.newArrayList();
	}
}