/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.refactoring.core;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.vclipse.refactoring.IRefactoringConfiguration;
import org.vclipse.refactoring.IRefactoringContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@SuppressWarnings("unchecked")
public class RefactoringConfiguration extends MethodCollector implements IRefactoringConfiguration {
	
	protected static final String INIT_PREFIX = "initialize_";
	
	protected static final String FEATURES_PREFIX = "features_";
	
	public RefactoringConfiguration() {
		collect(1, IRefactoringContext.class);
		collect(2);
	}
	
	public List<? extends EStructuralFeature> provideFeatures(IRefactoringContext context) {
		Object result = invoke(context, FEATURES_PREFIX);
		if(result instanceof List) {
			return (List<EStructuralFeature>)result;
		} else {
			return getFeatures(context.getSourceElement());
		}
	}
	
	public boolean initialize(IRefactoringContext context) {
		Object result = invoke(context, INIT_PREFIX);
		return result instanceof Boolean ? (Boolean)result : Boolean.FALSE;
	}
	
	protected List<EStructuralFeature> getFeatures(EObject object) {
		Map<String, EStructuralFeature> name2Feature = Maps.newHashMap();
		EClass eClass = object.eClass();
		for(EClass superType : eClass.getEAllSuperTypes()) {
			for(EStructuralFeature feature : superType.getEAllStructuralFeatures()) {
				name2Feature.put(feature.getName(), feature);
			}
		}
		for(EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
			name2Feature.put(feature.getName(), feature);
		}
		EReference containment = object.eContainmentFeature();
		if(containment != null) {
			name2Feature.put(containment.getName(), containment);			
		}
		return Lists.newArrayList(name2Feature.values());
	}
	
	protected List<? extends EStructuralFeature> get(EStructuralFeature ... features) {
		return Lists.newArrayList(features);
	}
}