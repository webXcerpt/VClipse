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

import com.google.inject.Inject
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.base.ImportUriExtractor
import org.vclipse.tests.VClipseTestPlugin
import org.vclipse.tests.VClipseTestResourceLoader
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.common.util.URI

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(VClipseTestPlugin))
class ImportUriExtractorTest extends XtextTest {
	
	@Inject
	private VClipseTestResourceLoader resourcesLoader
	
	@Inject
	private ImportUriExtractor uriExtractor
	
	new() {
		super(
			typeof(ImportUriExtractorTest).simpleName
		)
	}
	
	@Test
	def void test_ImportUriComputation_EMPTY() { // expecting an empty uri
		val resourceSet = new ResourceSetImpl
		val resource_one = resourceSet.createResource(URI::createURI("file:///c:/test.test"))
		val resource_two = resourceSet.createResource(URI::createPlatformResourceURI("test2.test", true));
		
		var extracted = uriExtractor.getImportUri(resource_one, resource_two)
		Assert::assertTrue(extracted, "".equals(extracted))
		extracted = uriExtractor.getImportUri(resource_two, resource_one)
		Assert::assertTrue(extracted, "".equals(extracted))
	}
	
	@Test
	def void test_ImportUriComputation_SAME_LENGTH() { // all tests should succeed
		val resource_one = resourcesLoader.getResource("/compare/added_vc_objects/VCML/car.vcml")
		val resource_two = resourcesLoader.getResource("/compare/added_vc_objects/VCML/engine.vcml")
		val resource_three = resourcesLoader.getResource("/compare/added_vc_objects/SAP/car.vcml")
		
		var extracted = uriExtractor.getImportUri(resource_one, resource_two)
		Assert::assertTrue(extracted, extracted.equals("car.vcml"))
		
		extracted = uriExtractor.getImportUri(resource_one, resource_one)
		Assert::assertTrue(extracted, extracted.equals(""))
		
		extracted = uriExtractor.getImportUri(resource_two, resource_one)
		Assert::assertTrue(extracted, extracted.equals("engine.vcml"))
		
		extracted = uriExtractor.getImportUri(resource_one, resource_three)
		Assert::assertTrue(extracted, extracted.equals("../VCML/car.vcml"))
		
		extracted = uriExtractor.getImportUri(resource_three, resource_one)
		Assert::assertTrue(extracted, extracted.equals("../SAP/car.vcml"))
		
		extracted = uriExtractor.getImportUri(resource_two, resource_three)
		Assert::assertTrue(extracted, extracted.equals("../VCML/engine.vcml"))
		
		extracted = uriExtractor.getImportUri(resource_three, resource_two)
		Assert::assertTrue(extracted, extracted.equals("../SAP/car.vcml"))
	}
	
	@Test
	def void test_ImportUriComputation_DIFFERENT_LENGTH() { // all tests should succeed(verified)
		val resource_one = resourcesLoader.getResource("/compare/added_vc_objects/VCML/car.vcml")
		val resource_two = resourcesLoader.getResource("/resources/VCML/car.vcml")
		
		var extracted = uriExtractor.getImportUri(resource_two, resource_one)
		Assert::assertTrue(extracted, extracted.equals("../../../resources/VCML/car.vcml"))
		
		extracted = uriExtractor.getImportUri(resource_one, resource_two)
		Assert::assertTrue(extracted, extracted.equals("../../compare/added_vc_objects/VCML/car.vcml"))
	}
}