/*******************************************************************************
 * Copyright (c) 2008 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.tests.base

import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Test
import org.junit.runner.RunWith

import org.vclipse.base.VClipseStrings
import org.vclipse.tests.VClipseTestResourceLoader
import junit.framework.Assert
import org.vclipse.vcml.vcml.VcmlPackage

@RunWith(typeof(XtextRunner))
class VClipseStringTest extends XtextTest {
	
	private VcmlPackage vcmlPackage
	
	new() {
		super(
			typeof(VClipseStringTest).simpleName
		)
	}
	
	override before() {
		vcmlPackage = VClipseTestResourceLoader::VCML_PACKAGE
	}
	
	@Test
	def void test_BOMItem() {
		var type = vcmlPackage.BOMItem
		var result = VClipseStrings::splitCamelCase(type.name)
		Assert::assertEquals(result.join(" "), 2, result.size)
		Assert::assertEquals(result.get(0), "BOM")
		Assert::assertEquals(result.get(1), "Item")
	}
	
	@Test
	def void test_MDataCharacteristic_C() {
		var type = vcmlPackage.MDataCharacteristic_C
		var result = VClipseStrings::splitCamelCase(type.name)
		Assert::assertEquals(result.join(" "), 4, result.size)
		Assert::assertEquals(result.get(0), "M")
		Assert::assertEquals(result.get(1), "Data")
		Assert::assertEquals(result.get(2), "Characteristic")
		Assert::assertEquals(result.get(3), "C")
	}
	
	@Test
	def void test_PFunction() {
		var type = vcmlPackage.PFunction
		var result = VClipseStrings::splitCamelCase(type.name)
		Assert::assertEquals(result.join(" "), 2, result.size)
		Assert::assertEquals(result.get(0), "P")
		Assert::assertEquals(result.get(1), "Function")
	}
	
	@Test
	def void test_BinaryCondition() {
		var type = vcmlPackage.binaryCondition
		var result = VClipseStrings::splitCamelCase(type.name)
		Assert::assertEquals(result.join(" "), 2, result.size)
		Assert::assertEquals(result.get(0), "Binary")
		Assert::assertEquals(result.get(1), "Condition")
	}
}