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
package org.vclipse.tests.refactoring

import com.google.inject.Inject
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.refactoring.core.RefactoringContext
import org.vclipse.refactoring.core.RefactoringType
import org.vclipse.refactoring.utils.EntrySearch
import org.vclipse.refactoring.utils.Labels
import org.vclipse.tests.VClipseTestResourceLoader

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(RefactoringInjectorProvider))
class LabelsTests extends XtextTest {
	
	@Inject
	private EntrySearch search
	
	@Inject
	private Labels labels
	
	@Inject
	private VClipseTestResourceLoader resourcesLoader
	
	new() {
		super(typeof(LabelsTests).simpleName)
	}
	
	@Test
	def test_UILabelProvider() {
		val entries = resourcesLoader.getResourceContents("/refactoring/Refactoring/car.vcml")
		var findEntry = search.findEntry("(300)CAR", VClipseTestResourceLoader::VCML_PACKAGE.class_, entries)
		Assert::assertNotNull(findEntry)
	
		var context = RefactoringContext::create(findEntry, null, RefactoringType::Extract)
		var uiLabel = labels.getUILabel(context)
		Assert::assertEquals("Extract ", uiLabel)
			
		context = RefactoringContext::create(findEntry, VClipseTestResourceLoader::VCML_PACKAGE.class_Characteristics, RefactoringType::Extract)
		uiLabel = labels.getUILabel(context)
		Assert::assertEquals("Extract characteristics ", uiLabel)
		
		findEntry = search.findEntry("PRECOND", VClipseTestResourceLoader::VCML_PACKAGE.precondition, entries)
		Assert::assertNotNull("precondition PRECOND not found", findEntry)
		context = RefactoringContext::create(findEntry, VClipseTestResourceLoader::VCML_PACKAGE.VCObject_Description, RefactoringType::Replace)
		uiLabel = context.label
		Assert::assertEquals("Replace description with a new value", uiLabel)
	}
}