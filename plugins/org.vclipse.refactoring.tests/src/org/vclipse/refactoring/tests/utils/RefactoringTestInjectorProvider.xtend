package org.vclipse.refactoring.tests.utils

import org.eclipse.xtext.junit4.IInjectorProvider
import org.vclipse.vcml.VCMLRuntimeModule
import com.google.inject.Guice

class RefactoringTestInjectorProvider implements IInjectorProvider {
 
	override getInjector() {
		val refactoringModule = new RefactoringTestModule
		val vcmlRuntimeModule = new VCMLRuntimeModule
		val injector = Guice::createInjector(refactoringModule, vcmlRuntimeModule)
		return injector
	}
}
