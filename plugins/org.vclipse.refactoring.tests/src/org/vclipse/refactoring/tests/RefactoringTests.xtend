/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.refactoring.tests

import com.google.inject.Inject
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.refactoring.tests.utils.RefactoringResourcesLoader
import org.vclipse.refactoring.tests.utils.RefactoringTestInjectorProvider
import org.vclipse.refactoring.utils.EntrySearch
import org.vclipse.refactoring.utils.Labels

import static org.junit.Assert.*
import static org.vclipse.refactoring.core.RefactoringContext.*
import static org.vclipse.refactoring.core.RefactoringType.*
import static org.vclipse.refactoring.tests.utils.RefactoringResourcesLoader.*
import org.vclipse.vcml.refactoring.VCMLRefactoring
import org.vclipse.refactoring.utils.Extensions

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(RefactoringTestInjectorProvider))
class RefactoringTests extends XtextTest {

	@Inject
	private EntrySearch search

	@Inject
	private Labels labels

	@Inject
	private RefactoringResourcesLoader resourcesLoader

	@Inject
	private Extensions extensions

	new() {
		super(typeof(RefactoringTests).simpleName)
	}

	@Test
	def refactoring_Divide() {
		var entries = resourcesLoader.getResourceContents(CAR_DESCRIPTION)
		var firstEntry = entries.get(0)
		val resource = firstEntry.eResource
		val refactoringExecuter = extensions.getInstance(typeof(VCMLRefactoring), firstEntry)
		if(refactoringExecuter == null) {
			fail("Can not find re-factoring executer for " + firstEntry)
		}
		
		
	}
}