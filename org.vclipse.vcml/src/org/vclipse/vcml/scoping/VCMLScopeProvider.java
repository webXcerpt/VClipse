/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
package org.vclipse.vcml.scoping;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.ConstraintObject;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.ShortVarDefinition;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantFunctionArgument;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping
 * on how and when to use it 
 *
 */
public class VCMLScopeProvider extends AbstractDeclarativeScopeProvider {
	
	// TODO write with declarative calls
	
	@Override
	public IScope getScope(EObject context, EReference reference) {
		final EClass type = (EClass)reference.getEType();
		if (type == VcmlPackage.eINSTANCE.getConstraintObject()) {
			final ConstraintSource constraintSource = containingConstraintSource(context);
			return Scopes.scopeFor(constraintSource.getObjects());
		} else if (type == VcmlPackage.eINSTANCE.getShortVarDefinition()) {
			final ConstraintSource constraintSource = containingConstraintSource(context);
			return Scopes.scopeFor(Iterables.concat(
					Iterables.transform(
							constraintSource.getObjects(), 
							new Function<ConstraintObject, Iterable<ShortVarDefinition>>() {
								public Iterable<ShortVarDefinition> apply(ConstraintObject constraintObject) {
									return constraintObject.getShortVars();
								}
							}
					)));
		} else if (reference == VcmlPackage.eINSTANCE.getFunction_Characteristics() &&
				context instanceof org.vclipse.vcml.vcml.Function) {
			VariantFunction vf = ((org.vclipse.vcml.vcml.Function)context).getFunction();
			return Scopes.scopeFor(Iterables.transform(
					vf.getArguments(),
					new Function<VariantFunctionArgument, Characteristic>() {
						public Characteristic apply(VariantFunctionArgument object) {
							return object.getCharacteristic();
						}
					})
			);
		} else if (reference == VcmlPackage.eINSTANCE.getTable_Characteristics() &&
				context instanceof org.vclipse.vcml.vcml.Table) {
			VariantTable vt = ((org.vclipse.vcml.vcml.Table)context).getTable();
			return Scopes.scopeFor(Iterables.transform(
					vt.getArguments(),
					new Function<VariantTableArgument, Characteristic>() {
						public Characteristic apply(VariantTableArgument object) {
							return object.getCharacteristic();
						}
					})
			);
		}
		return super.getScope(context, reference);
	}

	private ConstraintSource containingConstraintSource(EObject object) {
		while (object!=null && !VcmlPackage.eINSTANCE.getConstraintSource().isSuperTypeOf(object.eClass())) {
			object = object.eContainer();
		}
		return (ConstraintSource)object;
	}
	
}
