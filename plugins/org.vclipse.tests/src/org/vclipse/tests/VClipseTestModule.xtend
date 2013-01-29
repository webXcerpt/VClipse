/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.tests

import org.eclipse.xtext.service.AbstractGenericModule
import org.vclipse.tests.VClipseTestPlugin
import org.eclipse.ui.plugin.AbstractUIPlugin
import org.eclipse.jface.preference.IPreferenceStore

/*
 * Dependency injection module for the VClipse test plug-in.
 */
class VClipseTestModule extends AbstractGenericModule {

	protected VClipseTestPlugin plugin
	
	new(VClipseTestPlugin plugin) {
		this.plugin = plugin
	}
	
	def AbstractUIPlugin bindPlugin() {
		return plugin
	}
	
	def IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore();
	}
	
//	def Class<? extends IExtendedImageHelper> bindImageHelper() {
//		typeof(ClasspathAwareImageHelper)
//	}
//	
//	def Class<? extends IPreviewObjectComputer> bindPreviewObjectComputer() {
//		typeof(DefaultContainerPreviewComputer)
//	}
//	
//	def Class<? extends IRefactoringConfiguration> bindRefactoringConfiguration() {
//		typeof(RefactoringConfiguration)
//	}
//	
//	def Class<? extends IRefactoringUIConfiguration> bindRefactoringUIConfiguration() {
//		typeof(RefactoringUIConfiguration)
//	}
}