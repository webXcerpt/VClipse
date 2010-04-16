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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopedElement;
import org.eclipse.xtext.scoping.impl.DefaultScopeProvider;
import org.eclipse.xtext.scoping.impl.ImportUriUtil;
import org.eclipse.xtext.scoping.impl.ScopedElement;
import org.eclipse.xtext.scoping.impl.SimpleScope;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.ConstraintObject;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlPackage;
import org.vclipse.vcml.vcml.ShortVarDefinition;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantFunctionArgument;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping
 * on how and when to use it 
 *
 */
public class VCMLScopeProvider extends DefaultScopeProvider {
	
	@Override
	public IScope getScope(EObject context, EReference reference) {
		final EClass type = (EClass)reference.getEType();
		if (type == VcmlPackage.eINSTANCE.getConstraintObject()) {
			final ConstraintSource constraintSource = containingConstraintSource(context);
			return new HashedScope(IScope.NULLSCOPE, 
					Iterables.transform(
							constraintSource.getObjects(),
							new Function<ConstraintObject, IScopedElement>() {
								public IScopedElement apply(ConstraintObject object) {
									return ScopedElement.create(object.getName(), object);
								}
							}));
		} else if (type == VcmlPackage.eINSTANCE.getShortVarDefinition()) {
			final ConstraintSource constraintSource = containingConstraintSource(context);
			return new SimpleScope(IScope.NULLSCOPE, 
					Iterables.transform(
							Iterables.concat(
									Iterables.transform(
											constraintSource.getObjects(), 
											new Function<ConstraintObject, Iterable<ShortVarDefinition>>() {
												public Iterable<ShortVarDefinition> apply(ConstraintObject constraintObject) {
													return constraintObject.getShortVars();
												}
											}
									)),
							new Function<ShortVarDefinition, IScopedElement>() {
								public IScopedElement apply(ShortVarDefinition object) {
									return ScopedElement.create(object.getName(), object);
								}
							}));
		} else if (reference == VcmlPackage.eINSTANCE.getFunction_Characteristics() &&
				   context instanceof org.vclipse.vcml.vcml.Function) {
			VariantFunction vf = ((org.vclipse.vcml.vcml.Function)context).getFunction();
			return new SimpleScope(IScope.NULLSCOPE, 
					Iterables.transform(
							vf.getArguments(),
							new Function<VariantFunctionArgument, IScopedElement>() {
								public IScopedElement apply(VariantFunctionArgument object) {
									Characteristic characteristic = object.getCharacteristic();
									return ScopedElement.create(characteristic.getName(), characteristic);
								}
							})
			);
		} else if (reference == VcmlPackage.eINSTANCE.getTable_Characteristics() &&
				   context instanceof org.vclipse.vcml.vcml.Table) {
			VariantTable vt = ((org.vclipse.vcml.vcml.Table)context).getTable();
			return new SimpleScope(IScope.NULLSCOPE, 
					Iterables.transform(
							vt.getArguments(),
							new Function<VariantTableArgument, IScopedElement>() {
								public IScopedElement apply(VariantTableArgument object) {
									Characteristic characteristic = object.getCharacteristic();
									return ScopedElement.create(characteristic.getName(), characteristic);
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
	
	// adapted from Xtext org.eclipse.xtext.scoping.impl.DefaultScope

	@Override
	protected IScope createScope(Resource resource, EClass type) {
		return new SAPObjectScopeForResource(createParent(resource, type, getImportUriResolver()), resource, type);
	}

	private IScope createParent(Resource resource, EClass type, Function<EObject, String> importResolver) {
		Model model = getModel(resource);
		if (model==null) return IScope.NULLSCOPE;
		Set<String> uniqueImportURIs = new HashSet<String>(10);
		List<String> orderedImportURIs = new ArrayList<String>(10);
		for(Import imp : model.getImports()) {
			String uri = importResolver.apply(imp);
			if (uri != null && uniqueImportURIs.add(uri) && ImportUriUtil.isValid(imp, uri)) {
				orderedImportURIs.add(uri);
			}
		}
		IScope result = IScope.NULLSCOPE;
		for(int i = orderedImportURIs.size() - 1; i >= 0; i--) {
			result = new SAPObjectScopeForResource(result, ImportUriUtil.getResource(resource, orderedImportURIs.get(i)), type);
		}
		return result;
	}

	private class SAPObjectScopeForResource extends HashedScope {
		public SAPObjectScopeForResource(IScope parent, Resource resource, final EClass type) {
			super(parent, 
					Iterables.transform(
							Iterables.filter(
									getModel(resource).getObjects(),
									new Predicate<VCObject>() {
										public boolean apply(VCObject object) {
											return type.isSuperTypeOf(object.eClass());
										}
									}),
									new Function<VCObject, IScopedElement>() {
								public IScopedElement apply(VCObject object) {
									return ScopedElement.create(object.getName(), object);
								}
							}
				));
		}
	}
	
	private Model getModel(Resource resource) {
		return (Model)Iterables.find(resource.getContents(), new Predicate<EObject>() {
			public boolean apply(EObject object) {
				return VcmlPackage.eINSTANCE.getModel().isSuperTypeOf(object.eClass());
			}
		});
	}

}
