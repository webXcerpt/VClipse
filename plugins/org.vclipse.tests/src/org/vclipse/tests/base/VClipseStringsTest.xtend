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

@RunWith(typeof(XtextRunner))
class VClipseStringTest extends XtextTest {
	
	new() {
		super(typeof(VClipseStringTest).simpleName)
	}
	
	@Test
	def void test_SplitCamelCase() {
		var type = VClipseTestResourceLoader::VCML_PACKAGE.BOMItem
		var result = VClipseStrings::splitCamelCase(type.name)
		Assert::assertEquals(result.join(" "), 2, result.size)
		Assert::assertEquals(result.get(0), "BOM")
		Assert::assertEquals(result.get(1), "Item")
	}
}