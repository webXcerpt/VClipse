/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.utils

import org.vclipse.vcml.vcml.BillOfMaterial
import org.vclipse.vcml.vcml.CharacteristicType
import org.vclipse.vcml.vcml.Material
import org.vclipse.vcml.vcml.NumericType
import org.vclipse.vcml.vcml.VcmlFactory
import org.vclipse.vcml.vcml.VcmlPackage

import static org.vclipse.vcml.utils.CreateVcmlObjects.*
import static org.vclipse.vcml.utils.VCMLObjectUtils.*

class CreateVcmlObjects {
	
	protected static VcmlFactory VCML_FACTORY = VcmlFactory::eINSTANCE
	protected static VcmlPackage VCML_PACKAGE = VcmlPackage::eINSTANCE
	
	def Material create it : VcmlFactory::eINSTANCE.createMaterial newMaterial(String name, String description, String type) {
		it.name = name
		it.description = mkSimpleDescription(description)
		it.type = type
	}
	
	def BillOfMaterial create it : VcmlFactory::eINSTANCE.createBillOfMaterial newBom(Material material, String description) {
		it.material = material
		it.description = mkSimpleDescription(description)
	}
	
	def CharacteristicType newCharacteristicTypeInstance() {
		val symbolicType = VCML_FACTORY.createSymbolicType
		symbolicType.numberOfChars = 30
		symbolicType.caseSensitive = true
		symbolicType
	}
	
	def NumericType newNumericTypeInstance() {
		val numericType = VCML_FACTORY.createNumericType
		numericType.numberOfChars = 15
		numericType.decimalPlaces = 3
		numericType.negativeValuesAllowed = true
		numericType
	}
}