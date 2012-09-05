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

import com.google.common.collect.Maps;

public class RefactoringContext implements IRefactoringContext {

	private EObject object;
	
	private RefactoringType type;
	
	private Map<Object, Object> attributes;
	
	private EStructuralFeature feature;
	
	private String text;
	
	public static RefactoringContext getEmpty() {
		return new RefactoringContext();
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
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
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
}
