package org.vclipse.refactoring.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.refactoring.tests.utils.RefactoringResourcesLoader
import org.vclipse.refactoring.tests.utils.RefactoringTestInjectorProvider
import org.vclipse.refactoring.utils.EntrySearch

import static org.vclipse.refactoring.tests.utils.RefactoringResourcesLoader.*
import static com.google.common.collect.Lists.*
import static org.junit.Assert.*

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(RefactoringTestInjectorProvider))
class SearchTests extends XtextTest {
	
	@Inject
	private EntrySearch search
	
	@Inject
	private RefactoringResourcesLoader resourcesLoader
	
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
}