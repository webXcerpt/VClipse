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
package org.vclipse.tests

import com.google.common.collect.Lists
import java.io.InputStream
import org.eclipse.core.resources.IContainer
import org.eclipse.core.resources.IResource
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.Path
import org.eclipse.jdt.core.JavaCore
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot
import org.eclipse.ui.IEditorSite
import org.eclipse.ui.PlatformUI
import org.eclipse.xtext.util.StringInputStream
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.vclipse.base.ui.util.EditorUtilsExtensions

class SWTBotWorkspaceWorker extends XtextTest {

	protected SWTWorkbenchBot bot
	
	protected IProgressMonitor monitor
	
	/**
	 * Initialization
	 */
	override before() {
		super.before()
		bot = new SWTWorkbenchBot
		bot.perspectiveByLabel("Java").activate
		val welcomeView = bot.viewByTitle("Welcome")
		if(welcomeView != null) {
			welcomeView.close
		}
		monitor = EditorUtilsExtensions::progressMonitor
		bot.sleep(10000)
	}

	/**
	 * 
	 */
	override after() {
		bot.sleep(1000)
	}
	
	/**
	 * Removes all projects from the workspace
	 */
	def protected cleanWorkspace() {
		monitor.beginTask("Deleting all projects in the workspace", IProgressMonitor::UNKNOWN)
		val root = ResourcesPlugin::workspace.root
		for(project : root.projects) {
			if(project.accessible) {
				project.delete(true, monitor)
			}
			monitor.worked(1)
		}
		monitor.done
		bot.sleep(1000)
	}
	
	/*
	 * Extracts resources being used in the test plug-in itself to the workspace.
	 * They are placed in the project org.vclipse.tests, so one can write own SWTBot tests.
	 */
	def protected createProject() {
		val root = ResourcesPlugin::workspace.root
		var project = root.getProject("org.vclipse.tests")
		var monitor = new NullProgressMonitor as IProgressMonitor
		if(!project.accessible) {
			val activeEditor = PlatformUI::workbench.activeWorkbenchWindow.activePage.activeEditor
			if(activeEditor != null) {
				val site = activeEditor.getSite
				if(site instanceof IEditorSite) {
					val actionBars = (site as IEditorSite).getActionBars
					monitor = actionBars.statusLineManager.progressMonitor
				}
			}
			project.create(monitor)
			project.open(monitor)
		}
		
		val natures = Lists::newArrayList(JavaCore::NATURE_ID, "org.eclipse.pde.PluginNature")
		val description = project.description
		description.setNatureIds(natures)
		project.setDescription(description, monitor)
		project
	}
	
	/**
	 * Creates a folder with a given name in the parent container.
	 */
	def protected createFolder(IContainer parent, String name) {
		val folder = parent.getFolder(new Path(name))
		if(!folder.exists) {
			folder.create(true, true, new NullProgressMonitor)
		}
		folder
	}
	
	/**
	 * Creates a file with a given name in the parent container
	 */
	 def protected createFile(IContainer parent, String name, InputStream stream) {
	 	val file = parent.getFile(new Path(name))
	 	if(!file.exists) {
	 		file.create(
	 			if(stream == null) 
	 				new StringInputStream("")
	 			else
	 				stream
	 			, IResource::FORCE, new NullProgressMonitor
	 		)
	 	}
	 	file
	 }
}