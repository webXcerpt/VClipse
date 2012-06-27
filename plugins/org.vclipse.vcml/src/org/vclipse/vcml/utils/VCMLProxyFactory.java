/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.utils;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.impl.VcmlFactoryImpl;


public class VCMLProxyFactory extends VcmlFactoryImpl {

	protected static final VcmlFactory VCML = VcmlFactory.eINSTANCE;
	
	public static Characteristic createCharacteristicProxy(Resource resource, String name) {
		Characteristic obj = VCML.createCharacteristic();
		obj.setName(name);
		URI uri = resource.getURI();
		((InternalEObject)obj).eSetProxyURI(uri.appendFragment(name));
		return obj;
	}

	public static Class createClassProxy(Resource resource, String name) {
		Class obj = VCML.createClass();
		obj.setName(name);
		URI uri = resource.getURI();
		((InternalEObject)obj).eSetProxyURI(uri.appendFragment(name));
		return obj;
	}

	public static Constraint createConstraintProxy(Resource resource, String name) {
		Constraint obj = VCML.createConstraint();
		obj.setName(name);
		URI uri = resource.getURI();
		((InternalEObject)obj).eSetProxyURI(uri.appendFragment(name));
		return obj;
	}

	public static DependencyNet createDependencyNetProxy(Resource resource, String name) {
		DependencyNet obj = VCML.createDependencyNet();
		obj.setName(name);
		URI uri = resource.getURI();
		((InternalEObject)obj).eSetProxyURI(uri.appendFragment(name));
		return obj;
	}

	public static InterfaceDesign createInterfaceDesignProxy(Resource resource, String name) {
		InterfaceDesign obj = VCML.createInterfaceDesign();
		obj.setName(name);
		URI uri = resource.getURI();
		((InternalEObject)obj).eSetProxyURI(uri.appendFragment(name));
		return obj;
	}

	public static Material createMaterialProxy(Resource resource, String name) {
		Material obj = VCML.createMaterial();
		obj.setName(name);
		URI uri = resource.getURI();
		((InternalEObject)obj).eSetProxyURI(uri.appendFragment(name));
		return obj;
	}

	public static Precondition createPreconditionProxy(Resource resource, String name) {
		Precondition obj = VCML.createPrecondition();
		obj.setName(name);
		URI uri = resource.getURI();
		((InternalEObject)obj).eSetProxyURI(uri.appendFragment(name));
		return obj;
	}

	public static Procedure createProcedureProxy(Resource resource, String name) {
		Procedure obj = VCML.createProcedure();
		obj.setName(name);
		URI uri = resource.getURI();
		((InternalEObject)obj).eSetProxyURI(uri.appendFragment(name));
		return obj;
	}

	public static SelectionCondition createSelectionConditionProxy(Resource resource, String name) {
		SelectionCondition obj = VCML.createSelectionCondition();
		obj.setName(name);
		URI uri = resource.getURI();
		((InternalEObject)obj).eSetProxyURI(uri.appendFragment(name));
		return obj;
	}

	public static VariantFunction createVariantFunctionProxy(Resource resource, String name) {
		VariantFunction obj = VCML.createVariantFunction();
		obj.setName(name);
		URI uri = resource.getURI();
		((InternalEObject)obj).eSetProxyURI(uri.appendFragment(name));
		return obj;
	}

	public static VariantTable createVariantTableProxy(Resource resource, String name) {
		VariantTable obj = VCML.createVariantTable();
		obj.setName(name);
		URI uri = resource.getURI();
		((InternalEObject)obj).eSetProxyURI(uri.appendFragment(name));
		return obj;
	}

}
