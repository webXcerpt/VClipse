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
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.ChangeKind;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.eclipse.emf.ecore.change.ListChange;

import com.google.common.collect.Lists;

public class ContainerPreviewComputer extends DefaultContainerPreviewComputer {

	@Override
	public List<EObject> getExisting(EObject original, EObject refactored, FeatureChange featureChange) {
		return getPreviewObjects(original, featureChange, false);
	}
	
	@Override
	public List<EObject> getRefactored(EObject original, EObject refactored, FeatureChange featureChange) {
		return getPreviewObjects(refactored, featureChange, true);
	}
	
	@SuppressWarnings("unchecked")
	protected List<EObject> getPreviewObjects(EObject object, FeatureChange featureChange, boolean ignoreChangeDescriptions) {
		List<EObject> previewObjects = Lists.newArrayList();
		if(object == null) {
			return previewObjects;
		}
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
				for(ListChange listChange : listChanges) {
					ChangeKind changeType = listChange.getKind();
					if(ChangeKind.ADD_LITERAL == changeType) {
						EList<EObject> values = listChange.getReferenceValues();
						for(EObject current : values) {
							if(ignoreChangeDescriptions && current.eContainer() instanceof ChangeDescription) {
								continue;
							}
							previewObjects.add(current);					
						}
					} else if(ChangeKind.REMOVE_LITERAL == changeType || ChangeKind.MOVE_LITERAL == changeType) {
						if(value instanceof List<?>) {
							previewObjects.addAll((List<EObject>)value);
							break;
						}
					}
				}
				return previewObjects;
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
