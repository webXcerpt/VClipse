package org.vclipse.refactoring.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.refactoring.core.RefactoringType
import org.vclipse.refactoring.tests.utils.RefactoringResourcesLoader
import org.vclipse.refactoring.tests.utils.RefactoringTestInjectorProvider
import org.vclipse.refactoring.utils.EntrySearch
import org.vclipse.refactoring.utils.Extensions
import org.vclipse.vcml.refactoring.VCMLRefactoring

import static com.google.common.collect.Lists.*
import static org.junit.Assert.*
import static org.vclipse.refactoring.core.RefactoringContext.*
import static org.vclipse.refactoring.tests.utils.RefactoringResourcesLoader.*
import static org.vclipse.refactoring.core.DefaultRefactoringExecuter.*

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(RefactoringTestInjectorProvider))
class SearchTests extends XtextTest {
	
	@Inject
	private EntrySearch search
	
	@Inject
	private RefactoringResourcesLoader resourcesLoader
	
	@Inject
	private Extensions extensions;
	
	new() {
		super(typeof(SearchTests).simpleName)
	}	
	
	@Test
	def testFindObject() {
		val entries = resourcesLoader.getResourceContents(CAR_DESCRIPTION)
		assertTrue(!entries.empty)
		val entry = entries.get((entries.size / 2) + 1)
		var findEntry = search.findEntry(entry, entries)
		assertNotNull(findEntry)
		
		val jft = EcoreFactory::eINSTANCE.createEObject
		findEntry = search.findEntry(jft, entries)
		assertNull(findEntry)
	}
	
	@Test
	def testFindByTypeAndName() {
		val entries = resourcesLoader.getResourceContents(CAR_DESCRIPTION)
		assertTrue(!entries.empty)
		var findEntry = search.findEntry("SEL_COND", VCML_PACKAGE.getSelectionCondition, entries)
		assertNotNull(findEntry)
		findEntry = search.findEntry("SEL_COND", VCML_PACKAGE.getCharacteristic, entries)
		assertNotNull(findEntry)
		findEntry = search.findEntry("(300)CAR", VCML_PACKAGE.getClass_, entries)
		assertNotNull(findEntry)
		findEntry = search.findEntry("ENGINE_2400", VCML_PACKAGE.getMaterial, entries)
		assertNotNull(findEntry)
		findEntry = search.findEntry("ENGINE_2400", VCML_PACKAGE.getCharacteristic, entries)
		assertNull(findEntry);
		findEntry = search.findEntry("FUELCONSUMPTION", VCML_PACKAGE.getMaterial, entries)
		assertNull(findEntry)
	}
	
	@Test
	def testSearchByName() {
		val entries = resourcesLoader.getResourceContents(CAR_DESCRIPTION)
		assertTrue(!entries.empty)
		val entry = search.findEntry("DEP_NET", VCML_PACKAGE.dependencyNet, entries)
		assertNotNull(entry)
		val namedEntries = newArrayList(entry)
		assertEquals(1, namedEntries.size)
	}
	
	@Test
	def testWithRefactorings() {
		var entries = resourcesLoader.getResourceContents(CAR_DESCRIPTION)
		var firstEntry = entries.get(0)
		val resource = firstEntry.eResource
		val refactoringExecuter = extensions.getInstance(typeof(VCMLRefactoring), firstEntry)
		if(refactoringExecuter == null) {
			fail("can not find re-factoring executer for " + firstEntry)
		}
		var klass = search.findEntry("(300)CAR", VCML_PACKAGE.class_, entries) as org.vclipse.vcml.vcml.Class
		assertNotNull(klass)
		var mapped = klass.characteristics.toMap([current | current.name])
		assertTrue(mapped.keySet.contains("ENGINE"))
		 
		var cstic = search.findEntry("ENGINE", VCML_PACKAGE.characteristic, entries)
		assertNotNull(cstic)
		val context = create(cstic, VCML_PACKAGE.vcmlModel_Objects, RefactoringType::Replace)
		context.addAttribute(BUTTON_STATE, true)
		refactoringExecuter.refactoring_Replace_objects(context)
		firstEntry = resource.contents.get(0)
		entries = resourcesLoader.getAllEntries(firstEntry)
		cstic = search.findEntry("ENGINE", VCML_PACKAGE.characteristic, entries)
		assertNull("not existent after re-factoring", cstic)
		klass = search.findEntry("(300)CAR", VCML_PACKAGE.class_, entries) as org.vclipse.vcml.vcml.Class
		assertNotNull(klass)
		mapped = klass.characteristics.toMap([current | current.name])
		assertFalse(mapped.keySet.contains("ENGINE")) 
	}
}