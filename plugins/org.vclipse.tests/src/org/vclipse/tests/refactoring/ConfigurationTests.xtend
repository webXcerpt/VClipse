/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.tests.refactoring

import com.google.common.collect.Iterables
import com.google.inject.Inject
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.refactoring.IRefactoringConfiguration
import org.vclipse.refactoring.core.RefactoringContext
import org.vclipse.refactoring.core.RefactoringType
import org.vclipse.refactoring.utils.Extensions
import org.vclipse.tests.VClipseTestResourceLoader
import org.vclipse.vcml.vcml.Characteristic

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(RefactoringInjectorProvider))
class ConfigurationTests extends XtextTest {
	
	@Inject
	private Extensions extensions
	
	@Inject
	private VClipseTestResourceLoader resourcesLoader
	
	new() {
		super(typeof(ConfigurationTests).simpleName)
	}
	
	@Test
	def testRefactoringConfiguration() {
		val entries = resourcesLoader.getResourceContents("/refactoring/Refactoring/car.vcml")
		val iterator = Iterables::filter(entries, typeof(Characteristic)).iterator
		if(iterator.hasNext) {
			val entry = iterator.next
			val configuration = extensions.getInstance(typeof(IRefactoringConfiguration), entry)
			val context = RefactoringContext::create(entry, VClipseTestResourceLoader::VCML_PACKAGE.vcmlModel_Objects, RefactoringType::Replace)
			val initialize = configuration.initialize(context)
			Assert::assertEquals("context initialized", true, initialize)
		
			val features = configuration.provideFeatures(context)
			Assert::assertTrue(!features.empty)
			Assert::assertTrue(features.contains(VClipseTestResourceLoader::VCML_PACKAGE.vcmlModel_Objects))
		}
	}
}