/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *		webXcerpt Software GmbH - initial creator
 * 		(www.webXcerpt.com)
 */
package org.vclipse.tests.refactoring

import org.eclipse.xtext.junit4.IInjectorProvider
import org.vclipse.tests.VClipseTestPlugin
import org.vclipse.tests.VClipseTestModule
import org.vclipse.vcml.VCMLRuntimeModule

import static com.google.inject.Guice.*

import org.eclipse.xtext.util.Modules2

/*
 * Injector provider is used by test classes
 */
class RefactoringInjectorProvider implements IInjectorProvider {
 
	override getInjector() {
		val refactoringModule = new VClipseTestModule(VClipseTestPlugin::instance)
		val vcmlRuntimeModule = new VCMLRuntimeModule
		val injector = createInjector(
			Modules2::mixin(refactoringModule, vcmlRuntimeModule)
		)
		return injector
	}
}
	