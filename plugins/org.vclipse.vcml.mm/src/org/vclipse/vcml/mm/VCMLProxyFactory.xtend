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
package org.vclipse.vcml.mm

import org.eclipse.emf.ecore.InternalEObject
import org.eclipse.emf.ecore.resource.Resource
import org.vclipse.vcml.vcml.Characteristic
import org.vclipse.vcml.vcml.VcmlFactory
import org.vclipse.vcml.vcml.VCObject
import org.vclipse.vcml.vcml.Constraint
import org.vclipse.vcml.vcml.VariantTable
import org.vclipse.vcml.vcml.VariantFunction
import org.vclipse.vcml.vcml.SelectionCondition
import org.vclipse.vcml.vcml.Procedure
import org.vclipse.vcml.vcml.Precondition
import org.vclipse.vcml.vcml.Material
import org.vclipse.vcml.vcml.InterfaceDesign
import org.vclipse.vcml.vcml.DependencyNet

/**
 * Extensions of the VcmlFactory creating proxy objects with a given name in a given resource.
 */
public class VCMLProxyFactory {

	def Characteristic create it : VcmlFactory::eINSTANCE.createCharacteristic characteristicProxy(String name, Resource resource) {
		createProxy(it, name, resource)
	}
	
	def org.vclipse.vcml.vcml.Class create it : VcmlFactory::eINSTANCE.createClass classProxy(String name, Resource resource) {
		createProxy(it, name, resource)
	}
	
	def Constraint create it : VcmlFactory::eINSTANCE.createConstraint constraintProxy(String name, Resource resource) {
		createProxy(it, name, resource)
	}
	
	def VariantFunction create it : VcmlFactory::eINSTANCE.createVariantFunction variantFunctionProxy(String name, Resource resource) {
		createProxy(it, name, resource)
	}
	
	def VariantTable create it : VcmlFactory::eINSTANCE.createVariantTable variantTableProxy(String name, Resource resource) {
		createProxy(it, name, resource)
	}
	
	def SelectionCondition create it : VcmlFactory::eINSTANCE.createSelectionCondition selectionConditionProxy(String name, Resource resource) {
		createProxy(it, name, resource)
	}
	
	def Procedure create it : VcmlFactory::eINSTANCE.createProcedure procedureProxy(String name, Resource resource) {
		createProxy(it, name, resource)
	}
	
	def Precondition create it : VcmlFactory::eINSTANCE.createPrecondition preconditionProxy(String name, Resource resource) {
		createProxy(it, name, resource)
	}
	
	def Material create it : VcmlFactory::eINSTANCE.createMaterial materialProxy(String name, Resource resource) {
		createProxy(it, name, resource)
	}
	
	def InterfaceDesign create it : VcmlFactory::eINSTANCE.createInterfaceDesign interfaceDesignProxy(String name, Resource resource) {
		createProxy(it, name, resource)
	}
	
	def DependencyNet create it : VcmlFactory::eINSTANCE.createDependencyNet dependencyNetProxy(String name, Resource resource) {
		createProxy(it, name, resource)
	}
	
	def protected <T extends VCObject> createProxy(T object, String name, Resource resource) {
		object.name = name
		val uri = resource.URI.appendFragment(name)
		(object as InternalEObject).eSetProxyURI(uri)
		object
	}
}
