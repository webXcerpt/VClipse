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

import com.google.inject.Inject
import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.refactoring.core.DefaultRefactoringExecuter
import org.vclipse.refactoring.core.RefactoringContext
import org.vclipse.refactoring.core.RefactoringType
import org.vclipse.refactoring.utils.EntrySearch
import org.vclipse.refactoring.utils.Extensions
import org.vclipse.tests.VClipseTestResourceLoader
import org.vclipse.vcml.refactoring.VCMLRefactoring

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(RefactoringInjectorProvider))
class SearchTests extends XtextTest {
	
	@Inject
	private EntrySearch search
	
	@Inject
	private VClipseTestResourceLoader resourcesLoader
	
	@Inject
	private Extensions extensions
	
	new() {
		super(typeof(SearchTests).simpleName)
	}	
	
	@Test
	def void testFindObject() {
		val entries = resourcesLoader.getResourceContents("/refactoring/Refactoring/car.vcml")
		Assert::assertTrue(!entries.empty)
		val entry = entries.get((entries.size / 2) + 1)
		var findEntry = search.findEntry(entry, entries)
		Assert::assertNotNull(findEntry)
		
		val jft = EcoreFactory::eINSTANCE.createEObject
		findEntry = search.findEntry(jft, entries)
		Assert::assertNull(findEntry)
	}
	
	@Test
	def void testFindByTypeAndName() {
		val entries = resourcesLoader.getResourceContents("/refactoring/Refactoring/car.vcml")
		Assert::assertTrue(!entries.empty)
		var findEntry = search.findEntry("CAR_SELECTION", VClipseTestResourceLoader::VCML_PACKAGE.constraint, entries)
		Assert::assertNotNull(findEntry)
		findEntry = search.findEntry("NAME", VClipseTestResourceLoader::VCML_PACKAGE.getCharacteristic, entries)
		Assert::assertNotNull(findEntry)
		findEntry = search.findEntry("(300)CAR", VClipseTestResourceLoader::VCML_PACKAGE.getClass_, entries)
		Assert::assertNotNull(findEntry)
	}
	
	@Test
	def void testSearchByName() {
		val entries = resourcesLoader.getResourceContents("/refactoring/Refactoring/car.vcml")
		Assert::assertTrue(!entries.empty)
		val entry = search.findEntry("DEPENDENCY_NET", VClipseTestResourceLoader::VCML_PACKAGE.dependencyNet, entries)
		Assert::assertNull(entry)
	}
	
	@Test
	def void testWithRefactorings() {
		var entries = resourcesLoader.getResourceContents("/refactoring/Refactoring/car.vcml")
		var firstEntry = entries.get(0)
		val resource = firstEntry.eResource
		val refactoringExecuter = extensions.getInstance(typeof(VCMLRefactoring), firstEntry)
		if(refactoringExecuter == null) {
			Assert::fail("can not find re-factoring executer for " + firstEntry)
		}
		var klass = search.findEntry("(300)CAR", VClipseTestResourceLoader::VCML_PACKAGE.class_, entries) as org.vclipse.vcml.vcml.Class
		Assert::assertNotNull(klass)
		var mapped = klass.characteristics.toMap([current | current.name])
		Assert::assertTrue(mapped.keySet.contains("NAME"))
		 
		var cstic = search.findEntry("NAME", VClipseTestResourceLoader::VCML_PACKAGE.characteristic, entries)
		Assert::assertNotNull(cstic)
		val context = RefactoringContext::create(cstic, VClipseTestResourceLoader::VCML_PACKAGE.vcmlModel_Objects, RefactoringType::Replace)
		context.addAttribute(DefaultRefactoringExecuter::BUTTON_STATE, true)
		refactoringExecuter.refactoring_Replace_objects(context)
		firstEntry = resource.contents.get(0)
		entries = resourcesLoader.getAllEntries(firstEntry)
		cstic = search.findEntry("NAME", VClipseTestResourceLoader::VCML_PACKAGE.characteristic, entries)
		Assert::assertNull("not existent after re-factoring", cstic)
		klass = search.findEntry("(300)CAR", VClipseTestResourceLoader::VCML_PACKAGE.class_, entries) as org.vclipse.vcml.vcml.Class
		Assert::assertNotNull(klass)
		mapped = klass.characteristics.toMap([current | current.name])
		Assert::assertFalse(mapped.keySet.contains("NAME")) 
	}
}