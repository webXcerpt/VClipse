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

import org.eclipse.xtext.junit4.IInjectorProvider
import org.eclipse.xtext.util.Modules2
import org.vclipse.refactoring.RefactoringPlugin
import org.vclipse.refactoring.guice.RefactoringModule
import org.vclipse.vcml.VCMLRuntimeModule

import static com.google.inject.Guice.*

/*
 * Injector provider used by test classes being written for re-factoring plug-in
 */
class RefactoringInjectorProvider implements IInjectorProvider {
 
	override getInjector() {
		val injector = createInjector(
			Modules2::mixin(
				new RefactoringModule(RefactoringPlugin::instance), 
				new VCMLRuntimeModule
			)
		)
		return injector
	}
}
	