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

import org.junit.Test
import com.google.inject.Inject
import org.eclipse.xtext.junit4.InjectWith
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.vclipse.refactoring.IRefactoringConfiguration
import org.vclipse.refactoring.utils.Extensions
import org.vclipse.vcml.vcml.Characteristic

import static com.google.common.collect.Iterables.*
import static org.junit.Assert.*
import static org.vclipse.refactoring.core.RefactoringContext.*
import static org.vclipse.refactoring.core.RefactoringType.*
import static org.vclipse.tests.VClipseTestResourceLoader.*

import org.vclipse.tests.VClipseTestResourceLoader

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
		val entries = resourcesLoader.getResourceContents(CAR_DESCRIPTION)
		val iterator = filter(entries, typeof(Characteristic)).iterator
		if(iterator.hasNext) {
			val entry = iterator.next
			val configuration = extensions.getInstance(typeof(IRefactoringConfiguration), entry)
			val context = create(entry, VCML_PACKAGE.vcmlModel_Objects, Replace)
			val initialize = configuration.initialize(context)
			assertEquals("context initialized", true, initialize)
		
			val features = configuration.provideFeatures(context)
			assertTrue(!features.empty)
			assertTrue(features.contains(VCML_PACKAGE.vcmlModel_Objects))
		}
	}
}