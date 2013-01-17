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
package org.vclipse.refactoring.tests.swtbot

import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.JavaCore
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner
import org.eclipse.ui.IEditorSite
import org.eclipse.ui.PlatformUI
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.refactoring.tests.utils.RefactoringResourcesLoader
import org.vclipse.refactoring.tests.utils.RefactoringTestModule
import org.vclipse.refactoring.utils.EntrySearch
import org.vclipse.refactoring.utils.Extensions
import org.vclipse.vcml.VCMLRuntimeModule
import org.vclipse.vcml.refactoring.VCMLRefactoring

import static com.google.inject.Guice.*
import static junit.framework.Assert.*
import static org.vclipse.refactoring.tests.swtbot.Strings.*
import static org.vclipse.refactoring.tests.utils.RefactoringResourcesLoader.*

import com.google.common.collect.Lists

@RunWith(typeof(SWTBotJunit4ClassRunner))
class RefactoringTests extends XtextTest {

	private RefactoringResourcesLoader resourcesLoader
	private Extensions extensions
	private VCMLRefactoring vcmlRefactoring
	private EntrySearch search
	
	private SWTWorkbenchBot bot
	
	new() {
		super(typeof(RefactoringTests).simpleName)
	}
	
	override before() {
		super.before()
		bot = new SWTWorkbenchBot();
		bot.perspectiveByLabel("Java").activate();
		
		val welcomeView = bot.viewByTitle("Welcome");
		if(welcomeView != null) {
			welcomeView.close();
		}
		
		val refactoringModule = new RefactoringTestModule
		val vcmlRuntimeModule = new VCMLRuntimeModule
		val injector = createInjector(refactoringModule, vcmlRuntimeModule)
		
		resourcesLoader = injector.getInstance(typeof(RefactoringResourcesLoader))
		extensions = injector.getInstance(typeof(Extensions))
		vcmlRefactoring = injector.getInstance(typeof(VCMLRefactoring))
		search = injector.getInstance(typeof(EntrySearch))
	}

	override after() {
		bot.sleep(1000);
	}
	
	def private void loadResources() {
		val root = ResourcesPlugin::workspace.root
		var project = root.getProject("org.vclipse.refactoring")
		var monitor = new NullProgressMonitor as IProgressMonitor
		if(!project.accessible) {
			val activeEditor = PlatformUI::workbench.activeWorkbenchWindow.activePage.activeEditor
			if(activeEditor != null) {
				val site = activeEditor.getSite();
				if(site instanceof IEditorSite) {
					val actionBars = (site as IEditorSite).getActionBars()
					monitor = actionBars.statusLineManager.progressMonitor
				}
			}
			project.create(monitor)
			project.open(monitor)
		}
		
		val description = project.description
		description.setNatureIds(Lists::newArrayList(JavaCore::NATURE_ID, "org.eclipse.pde.PluginNature"))
		project.setDescription(description, monitor)
		
		val folder = project.getFolder("car_description-dep")
		if(!folder.accessible) {
			folder.create(true, true, monitor)
		}
		
		var file = project.getFile("car_description.vcml")
		if(!file.accessible) {
			val stream = resourcesLoader.getInputStream("car_description.vcml")
			file.create(stream, true, monitor)
		}
		
		file = folder.getFile("CS_CAR1.cons")
		if(!file.accessible) {
			val stream = resourcesLoader.getInputStream("CS_CAR1.cons")
			file.create(stream, true, monitor)
		}
		
		file = folder.getFile("PRECOND.pre")
		if(!file.accessible) {
			val stream = resourcesLoader.getInputStream("PRECOND.pre")
			file.create(stream, true, monitor)
		}
		
		file = folder.getFile("PROC.proc")
		if(!file.accessible) {
			val stream = resourcesLoader.getInputStream("PROC.proc")
			file.create(stream, true, monitor)
		}
		
		file = folder.getFile("SEL_COND.sel")
		if(!file.accessible) {
			val stream = resourcesLoader.getInputStream("SEL_COND.sel")
			file.create(stream, true, monitor)
		}
	}
	
	@Test
	def refactoring_Split() {
		loadResources
		val description_root = resourcesLoader.getResourceRoot("/car_description.vcml")
		assertNotNull(description_root)
	}
}