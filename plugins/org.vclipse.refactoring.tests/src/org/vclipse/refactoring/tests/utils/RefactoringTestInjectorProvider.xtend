package org.vclipse.refactoring.tests.utils

import org.eclipse.xtext.junit4.IInjectorProvider
import org.vclipse.vcml.VCMLRuntimeModule
import static com.google.inject.Guice.*

class RefactoringTestInjectorProvider implements IInjectorProvider {
 
	override getInjector() {
		val refactoringModule = new RefactoringTestModule
		val vcmlRuntimeModule = new VCMLRuntimeModule
		val injector = createInjector(refactoringModule, vcmlRuntimeModule)
		return injector
	}
}
