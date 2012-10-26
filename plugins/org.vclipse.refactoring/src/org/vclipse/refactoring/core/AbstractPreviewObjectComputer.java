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
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.eclipse.emf.ecore.change.ListChange;

import com.google.common.collect.Lists;

public abstract class AbstractPreviewObjectComputer extends DefaultPreviewObjectComputer {

	@Override
	public List<EObject> getExisting(EObject original, EObject refactored, FeatureChange featureChange) {
		return getPreviewObjects(original, featureChange);
	}
	
	@Override
	public List<EObject> getRefactored(EObject original, EObject refactored, FeatureChange featureChange) {
		return getPreviewObjects(refactored, featureChange);
	}
	
	@SuppressWarnings("unchecked")
	protected List<EObject> getPreviewObjects(EObject object, FeatureChange featureChange) {
		List<EObject> previewObjects = Lists.newArrayList();
		EClass type = object.eClass();		
		Set<EClass> ignoreTypes = getIgnoreTypes();
		Set<EClass> favoredTypes = getFavoredTypes();
		if(containsType(type, ignoreTypes)) {
			EStructuralFeature feature = featureChange.getFeature();
			Object value = object.eGet(feature);
			EList<ListChange> listChanges = featureChange.getListChanges();
			if(listChanges.isEmpty()) {
				if(value instanceof List<?>) {
					previewObjects.addAll((List<EObject>)value);
					return previewObjects;
				} else if(value instanceof EObject) {
					EObject entry = (EObject)value;
					if(containsType(entry.eClass(), favoredTypes)) {
						previewObjects.add(entry);
						return previewObjects;
					}
				} else {
					return previewObjects;
				}
			} else {
				ListChange listChange = listChanges.get(0);
				int index = listChange.getIndex();
				EList<EObject> entries = (EList<EObject>)value;
				if(index < entries.size()) {
					EObject entry = entries.get(index);
					previewObjects.add(entry);
					return previewObjects;
				}
			}
		} else {
			EObject bottomUp = bottomUp(object);
			previewObjects.add(bottomUp);
			return previewObjects;
		}
		return previewObjects;
	}
	
	protected EObject bottomUp(EObject source) {
		EObject container = source;
		Set<EClass> ignoreTypes = getIgnoreTypes();
		Set<EClass> favoredTypes = getFavoredTypes();
		while(container != null) {
			EClass type = container.eClass();
			if(containsType(type, ignoreTypes)) {
				return source;
			} else if(containsType(type, favoredTypes)) {
				return container;
			}
			container = container.eContainer();
		}
		return container;
	}
	
	protected boolean containsType(EClass type, Set<EClass> types) {
		if(types.contains(type)) {
			return true;
		} else {
			for(EClass superType : type.getEAllSuperTypes()) {
				if(types.contains(superType)) {
					return true;
				}
			}
			return false;
		}
	}
}
