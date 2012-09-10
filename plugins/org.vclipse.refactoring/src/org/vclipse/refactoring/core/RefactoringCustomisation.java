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

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.common.collect.Lists;

@SuppressWarnings("unchecked")
public class RefactoringCustomisation extends MethodCollector {
	
	public RefactoringCustomisation() {
		collect(1, IRefactoringContext.class);
		collect(2);
	}
	 
	public boolean evaluate(IRefactoringContext context) {
		Object result = invoke(context, "evaluate_");
		return result instanceof Boolean ? (Boolean)result : Boolean.FALSE;
	}
	
	public List<? extends EStructuralFeature> features(IRefactoringContext context) {
		Object result = invoke(context, "features_");
		if(result instanceof List) {
			return (List<EStructuralFeature>)result;
		} else {
			enableFiltering();
			return getFeatures(context.getSourceElement());
		}
	}
	
	protected List<EStructuralFeature> getFeatures(EObject object) {
		List<EStructuralFeature> features = Lists.newArrayList();
		EClass eClass = object.eClass();
		for(EClass superType : eClass.getEAllSuperTypes()) {
			features.addAll(superType.getEAllStructuralFeatures());
		}
		features.addAll(eClass.getEAllStructuralFeatures());
		return features;
	}
}