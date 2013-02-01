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
package org.vclipse.tests.refactoring.swtbot

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.refactoring.utils.EntrySearch
import org.vclipse.refactoring.utils.Extensions
import org.vclipse.tests.SWTBotWorkspaceWorker
import org.vclipse.tests.VClipseTestUtilities
import org.vclipse.tests.refactoring.RefactoringInjectorProvider
import org.vclipse.vcml.refactoring.VCMLRefactoring

@RunWith(typeof(SWTBotJunit4ClassRunner))
class RegressionRefactoringTest extends SWTBotWorkspaceWorker {

	private VClipseTestUtilities resourcesLoader
	
	private Extensions extensions
	private VCMLRefactoring vcmlRefactoring
	private EntrySearch search

	override before() {
		super.before()
		val injector = (new RefactoringInjectorProvider).injector
		resourcesLoader = injector.getInstance(typeof(VClipseTestUtilities))
		extensions = injector.getInstance(typeof(Extensions))
		vcmlRefactoring = injector.getInstance(typeof(VCMLRefactoring))
		search = injector.getInstance(typeof(EntrySearch))
		cleanWorkspace
		createProject
	}
	
	override protected createProject() {
		val project = super.createProject()
		
		var folder = createFolder(project, "car-dep")
		var file = createFile(folder, "CAR_SELECTION.cons", 
			resourcesLoader.getInputStream("/refactoring/Refactoring/car-dep/CAR_SELECTION.cons")
		)
		
		folder = createFolder(project, "engine-dep")
		file = createFile(folder, "TYPE_SELECTION.cons",
			resourcesLoader.getInputStream("/refactoring/Refactoring/engine-dep/TYPE_SELECTION.cons")
		)
		
		file = createFile(project, "car.vcml", 
			resourcesLoader.getInputStream("/refactoring/Refactoring/car.vcml")
		)
		
		file = createFile(project, "engine.vcml", 
			resourcesLoader.getInputStream("/refactoring/Refactoring/engine.vcml")
		)
		bot.sleep(10000)
		project
	}
	
	@Test
	def void test() {
		bot.sleep(10000)
	}
}