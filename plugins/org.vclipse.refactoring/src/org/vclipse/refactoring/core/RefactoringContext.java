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

import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.ui.shared.Access;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.utils.Labels;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class RefactoringContext implements IRefactoringContext {

	private EObject object;
	private EStructuralFeature feature;
	private int index;
	
	private RefactoringType type;
	private Map<Object, Object> attributes;

	private String text;
	
	@Inject
	private static Provider<RefactoringContext> contextProvider;
	
	@Inject
	private Labels labels;
	
	public static RefactoringContext getEmpty() {
		if(contextProvider == null) {
			contextProvider = Access.provider(RefactoringContext.class);
		}
		return contextProvider.get();
	}
	
	public static RefactoringContext create(EObject object, EStructuralFeature feature, RefactoringType type) {
		RefactoringContext empty = getEmpty();
		empty.setSourceElement(object);
		empty.setStructuralFeature(feature);
		empty.setType(type);
		return empty;
	}
	
	public RefactoringContext() {
		attributes = Maps.newHashMap();
	}
	
	public String getLabel() {
		if(text == null || text.isEmpty()) {
			return labels.getUILabel(this);			
		}
		return text;
	}
	
	public void setLabel(String text) {
		this.text = text;
	}
	
	@Override
	public String getDescription() {
		StringBuffer buffer = new StringBuffer();
		if(object != null) {
			buffer.append(type.name() + "_");
			buffer.append(object.eClass().getName());
			if(feature != null) {
				buffer.append("_" + feature.getName());
			}
		}
		return buffer.toString();
	}
	
	@Override
	public void setSourceElement(EObject object) {
		this.object = object;
	}

	@Override
	public EObject getSourceElement() {
		return object;
	}
	
	@Override
	public void setStructuralFeature(EStructuralFeature feature) {
		this.feature = feature;
	}

	@Override
	public EStructuralFeature getStructuralFeature() {
		return feature;
	}

	@Override
	public RefactoringType getType() {
		return type;
	}

	@Override
	public void setType(RefactoringType type) {
		this.type = type;
	}	
	
	@Override
	public void addAttribute(Object key, Object value) {
		attributes.put(key, value);
	}
	
	public Map<Object, Object> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	@Override
	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
