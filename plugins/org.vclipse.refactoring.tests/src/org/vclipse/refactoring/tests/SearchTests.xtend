package org.vclipse.refactoring.tests

import com.google.common.collect.Lists
import com.google.inject.Inject
import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.refactoring.tests.utils.RefactoringTest
import org.vclipse.refactoring.tests.utils.RefactoringTestInjectorProvider
import org.vclipse.refactoring.utils.EntrySearch

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(RefactoringTestInjectorProvider))
class SearchTests extends RefactoringTest {
	
	@Inject
	private EntrySearch search
	
	new() {
		super(typeof(SearchTests).simpleName)
	}	
	
	@Test
	def testFindObject() {
		Assert::assertTrue(!entries.isEmpty());
		val size = entries.size();
		val entry = entries.get((size / 2) + 1);
		var findEntry = search.findEntry(entry, entries);
		Assert::assertNotNull(findEntry);
		
		val jft = EcoreFactory::eINSTANCE.createEObject();
		findEntry = search.findEntry(jft, entries);
		Assert::assertNull(findEntry);
	}
	
	@Test
	def testFindByTypeAndName() {
		Assert::assertTrue(!entries.isEmpty());
		var findEntry = search.findEntry("SEL_COND", VCML_PACKAGE.getSelectionCondition(), entries);
		Assert::assertNotNull(findEntry);
		findEntry = search.findEntry("SEL_COND", VCML_PACKAGE.getCharacteristic(), entries);
		Assert::assertNotNull(findEntry);
		findEntry = search.findEntry("(300)CAR", VCML_PACKAGE.getClass_(), entries);
		Assert::assertNotNull(findEntry);
		findEntry = search.findEntry("ENGINE_2400", VCML_PACKAGE.getMaterial(), entries);
		Assert::assertNotNull(findEntry);
		findEntry = search.findEntry("ENGINE_2400", VCML_PACKAGE.getCharacteristic(), entries);
		Assert::assertNull(findEntry);
		findEntry = search.findEntry("FUELCONSUMPTION", VCML_PACKAGE.getMaterial(), entries);
		Assert::assertNull(findEntry);
	}
	
	@Test
	def testSearchByName() {
		Assert::assertTrue(!entries.isEmpty());
		val namedIterable = search.getEntries("DEP_NET", entries);
		Assert::assertNotNull(namedIterable);
		val namedEntries = Lists::newArrayList(namedIterable);
		Assert::assertEquals(1, namedEntries.size());
	}

	@Test
	def testSearchByType() {
		val classes = Lists::newArrayList(search.getEntries(VCML_PACKAGE.getClass_(), entries));
		Assert::assertEquals("Amount of classes", 1, classes.size());
		val cstics = Lists::newArrayList(search.getEntries(VCML_PACKAGE.getCharacteristic(), entries));
		Assert::assertEquals("Amount of cstics", 9, cstics.size());
	}
}