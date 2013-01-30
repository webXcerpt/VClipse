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
import org.eclipse.xtext.junit4.InjectWith
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest

import org.vclipse.refactoring.utils.EntrySearch
import org.vclipse.refactoring.utils.Labels

import static org.junit.Assert.*
import static org.vclipse.refactoring.core.RefactoringContext.*
import static org.vclipse.refactoring.core.RefactoringType.*
import static org.vclipse.tests.VClipseTestResourceLoader.*

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
//		val entries = resourcesLoader.getResourceContents(CAR_DESCRIPTION)
//		var findEntry = search.findEntry("(300)CAR", VCML_PACKAGE.class_, entries)
//		assertNotNull(findEntry)
//	
//		var context = create(findEntry, null, Extract)
//		var uiLabel = labels.getUILabel(context)
//		assertEquals("Extract ", uiLabel)
//			
//		context = create(findEntry, VCML_PACKAGE.class_Characteristics, Extract)
//		uiLabel = labels.getUILabel(context)
//		assertEquals("Extract characteristics ", uiLabel)
//		
//		findEntry = search.findEntry("PRECOND", VCML_PACKAGE.precondition, entries)
//		assertNotNull("precondition PRECOND not found", findEntry)
//		context = create(findEntry, VCML_PACKAGE.VCObject_Description, Replace)
//		uiLabel = context.label
//		assertEquals("Replace description with a new value", uiLabel)
	}
}