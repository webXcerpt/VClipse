/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.refactoring.core;

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
		collect(1, IRefactoringContext.class);
		collect(2);
	}
	 
	public boolean evaluate(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		String prefix = EVALUATION_PREFIX + context.getType();
		Pair<EObject, Method> pair = getMethod(element, context.getStructuralFeature(), prefix);
		if(pair != null) {
			try {
				Method method = pair.getSecond();
				List<Object> params = Lists.newArrayList();
				if(method.getParameterTypes().length == 1) {
					params.add(context);
				} else if(method.getParameterTypes().length == 2) {
					params.add(context);
					params.add(pair.getFirst());
				}
				Object result = pair.getSecond().invoke(this, params.toArray());
				return (Boolean)result;
			} catch (Exception e) {
				RefactoringPlugin.log(e.getMessage(), e);
			}
		} 
		return Boolean.FALSE;
	}
	
	public List<? extends EStructuralFeature> features(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		String prefix = FEATURES_PREFIX + context.getType();
		Pair<EObject, Method> pair = getMethod(element, context.getStructuralFeature(), prefix);
		if(pair != null) {
			try {
				return (List<EStructuralFeature>)pair.getSecond().invoke(this, new Object[]{context, pair.getFirst()});
			} catch (Exception e) {
				RefactoringPlugin.log(e.getMessage(), e);
			}
		} 
		return Lists.newArrayList();
	}
}