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
package org.vclipse.constraint.scoping;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.vclipse.dependency.scoping.DependencyScopeProvider;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Classification;
import org.vclipse.vcml.vcml.ConstraintClass;
import org.vclipse.vcml.vcml.ConstraintMaterial;
import org.vclipse.vcml.vcml.ConstraintObject;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.ObjectCharacteristicReference;
import org.vclipse.vcml.vcml.ObjectType;
import org.vclipse.vcml.vcml.PartialKey;
import org.vclipse.vcml.vcml.ShortVarDefinition;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping
 * on how and when to use it 
 *
 */
public class ConstraintScopeProvider extends DependencyScopeProvider {

	IScope scope_ConstraintObject(ConstraintSource context, EReference ref) {
		return Scopes.scopeFor(context.getObjects());
	}
	
	// TODO verify this
	IScope scope_ShortVarDefinition_ref(ConstraintSource context, EReference ref) {
		return Scopes.scopeFor(Iterables.concat(
				Iterables.transform(
						context.getObjects(), 
						new Function<ConstraintObject, Iterable<ShortVarDefinition>>() {
							public Iterable<ShortVarDefinition> apply(ConstraintObject constraintObject) {
								return constraintObject.getShortVars();
							}
						}
				)));
	}
	
	IScope scope_ObjectCharacteristicReference_characteristic(ObjectCharacteristicReference context, EReference ref) {
		ConstraintObject constraintObject = context.getLocation();
		if (constraintObject instanceof ConstraintClass) {
			ConstraintClass cc = (ConstraintClass)constraintObject;
			return createCsticScope(Collections.singletonList(cc.getClass_()));
		} else if (constraintObject instanceof ConstraintMaterial) {
			ConstraintMaterial cm = (ConstraintMaterial)constraintObject;
			ObjectType objectType = cm.getObjectType();
			List<PartialKey> attrs = objectType.getAttrs();
			PartialKey partialKey = attrs.get(0);
			Material material = partialKey.getMaterial();
			List<Classification> classifications = material.getClassifications();
			Iterable<Class> classes = Iterables.transform(classifications, new Function<Classification, Class> () {
				public Class apply(Classification clf) {
					return clf.getCls();
				}
			});
			return createCsticScope(classes);
		}
		return null;
	}

	private IScope createCsticScope(Iterable<Class> classes) {
		Class cls = Iterables.getFirst(classes, null);
		Iterable<Class> restClasses = Iterables.skip(classes, 1);
		if (cls.getSuperClasses().isEmpty() && Iterables.isEmpty(restClasses)) {
			return Scopes.scopeFor(cls.getCharacteristics());
		} else {
			return Scopes.scopeFor(cls.getCharacteristics(), createCsticScope(Iterables.concat(cls.getSuperClasses(), restClasses)));
		}
	}
	
}
