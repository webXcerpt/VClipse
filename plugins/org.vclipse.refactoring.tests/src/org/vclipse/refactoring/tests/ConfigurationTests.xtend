package org.vclipse.refactoring.tests

import com.google.inject.Inject
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.refactoring.IRefactoringConfiguration
import org.vclipse.refactoring.core.RefactoringContext
import org.vclipse.refactoring.core.RefactoringType
import org.vclipse.refactoring.tests.utils.RefactoringTest
import org.vclipse.refactoring.tests.utils.RefactoringTestInjectorProvider
import org.vclipse.refactoring.utils.Extensions
import com.google.common.collect.Iterables
import org.vclipse.vcml.vcml.Characteristic

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(RefactoringTestInjectorProvider))
class ConfigurationTests extends RefactoringTest {
	
	@Inject
	private Extensions extensions
	
	new() {
		super(typeof(ConfigurationTests).simpleName)
	}
	
	@Test
	def testRefactoringConfiguration() {
		val iterator = Iterables::filter(entries, typeof(Characteristic)).iterator
		if(iterator.hasNext) {
			val entry = iterator.next
			val configuration = extensions.getInstance(typeof(IRefactoringConfiguration), entry)
			val context = RefactoringContext::create(entry, VCML_PACKAGE.vcmlModel_Objects, RefactoringType::Replace)
			val initialize = configuration.initialize(context)
			Assert::assertEquals("context initialized", true, initialize)
		
			val features = configuration.provideFeatures(context)
			Assert::assertTrue(!features.empty)
			Assert::assertTrue(features.contains(VCML_PACKAGE.vcmlModel_Objects))
		}
	}
}