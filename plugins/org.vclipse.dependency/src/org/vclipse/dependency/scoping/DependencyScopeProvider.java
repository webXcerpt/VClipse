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
package org.vclipse.dependency.scoping;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantFunctionArgument;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping
 * on how and when to use it 
 *
 */
public class DependencyScopeProvider extends AbstractDeclarativeScopeProvider {

	IScope scope_Function_characteristics(org.vclipse.vcml.vcml.Function context, EReference ref) {
		VariantFunction vf = context.getFunction();
		return Scopes.scopeFor(Iterables.transform(
				vf.getArguments(),
				new Function<VariantFunctionArgument, Characteristic>() {
					public Characteristic apply(VariantFunctionArgument object) {
						return object.getCharacteristic();
					}
				})
				);
	}

	IScope scope_Table_characteristics(org.vclipse.vcml.vcml.Table context, EReference ref) {
		VariantTable vt = context.getTable();
		return Scopes.scopeFor(Iterables.transform(
				vt.getArguments(),
				new Function<VariantTableArgument, Characteristic>() {
					public Characteristic apply(VariantTableArgument object) {
						return object.getCharacteristic();
					}
				})
				);
	}
	
}
