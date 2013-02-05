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
package org.vclipse.vcml

import org.vclipse.vcml.vcml.CharacteristicValue
import org.vclipse.vcml.vcml.SimpleDescription
import org.vclipse.vcml.vcml.SimpleDocumentation
import org.vclipse.vcml.vcml.VcmlFactory
import org.vclipse.vcml.vcml.VcmlPackage

/**
 * Extensions for {@link org.vclipse.vcml.vcml.VcmlFactory}.
 * 
 * Some of them create cached instances and other a new instance on each call.
 */
class VCMLFactoryExtension {
	
	public VcmlFactory VCML_FACTORY
	public VcmlPackage VCML_PACKAGE
	
	new() {
		VCML_FACTORY = VcmlFactory::eINSTANCE
		VCML_PACKAGE = VcmlPackage::eINSTANCE
	}
	
	def CharacteristicValue newCharacteristicValue(String name) {
		characteristicValue(name, null, null, false)
	}
	
	def CharacteristicValue create it : VcmlFactory::eINSTANCE.createCharacteristicValue characteristicValue(String name, String description, String documentation, boolean _default) {
		it.name = name
		it.^default = ^default
		if(description != null) {
			it.description = newSimpleDescription(description)
		}
		if(documentation != null) {
			it.documentation = newSimpleDocumentation(documentation)
		}
		it.dependencies = VCML_FACTORY.createCharacteristicOrValueDependencies
	}
	
	def SimpleDescription newSimpleDescription(String description) {
		val simpleDescription = VCML_FACTORY.createSimpleDescription
		simpleDescription.setValue(description)
		return simpleDescription
	}
	
	def SimpleDocumentation newSimpleDocumentation(String documentation) {
		val simpleDocumentation = VCML_FACTORY.createSimpleDocumentation
		simpleDocumentation.setValue(documentation)
		return simpleDocumentation
	}
}