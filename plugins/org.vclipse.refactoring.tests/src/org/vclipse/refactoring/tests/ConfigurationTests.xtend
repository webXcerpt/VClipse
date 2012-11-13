package org.vclipse.refactoring.tests

import com.google.inject.Inject
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.refactoring.IRefactoringConfiguration
import org.vclipse.refactoring.tests.utils.RefactoringResourcesLoader
import org.vclipse.refactoring.tests.utils.RefactoringTestInjectorProvider
import org.vclipse.refactoring.utils.Extensions
import org.vclipse.vcml.vcml.Characteristic

import static com.google.common.collect.Iterables.*
import static org.junit.Assert.*
import static org.vclipse.refactoring.core.RefactoringContext.*
import static org.vclipse.refactoring.core.RefactoringType.*
import static org.vclipse.refactoring.tests.utils.RefactoringResourcesLoader.*

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(RefactoringTestInjectorProvider))
class ConfigurationTests extends XtextTest {
	
	@Inject
	private Extensions extensions
	
	@Inject
	private RefactoringResourcesLoader resourcesLoader
	
	new() {
		super(typeof(ConfigurationTests).simpleName)
	}
	
	@Test
	def testRefactoringConfiguration() {
		val entries = resourcesLoader.getResourceContents(CAR_DESCRIPTION)
		val iterator = filter(entries, typeof(Characteristic)).iterator
		if(iterator.hasNext) {
			val entry = iterator.next
			val configuration = extensions.getInstance(typeof(IRefactoringConfiguration), entry)
			val context = create(entry, VCML_PACKAGE.vcmlModel_Objects, Replace)
			val initialize = configuration.initialize(context)
			assertEquals("context initialized", true, initialize)
		
			val features = configuration.provideFeatures(context)
			assertTrue(!features.empty)
			assertTrue(features.contains(VCML_PACKAGE.vcmlModel_Objects))
		}
	}
}