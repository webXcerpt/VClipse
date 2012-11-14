package org.vclipse.refactoring.tests

import com.google.inject.Inject
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.refactoring.tests.utils.RefactoringResourcesLoader
import org.vclipse.refactoring.tests.utils.RefactoringTestInjectorProvider
import org.vclipse.refactoring.utils.EntrySearch
import org.vclipse.refactoring.utils.Labels

import static org.junit.Assert.*
import static org.vclipse.refactoring.core.RefactoringContext.*
import static org.vclipse.refactoring.core.RefactoringType.*
import static org.vclipse.refactoring.tests.utils.RefactoringResourcesLoader.*

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(RefactoringTestInjectorProvider))
class LabelsTests extends XtextTest {
	
	@Inject
	private EntrySearch search
	
	@Inject
	private Labels labels
	
	@Inject
	private RefactoringResourcesLoader resourcesLoader
	
	new() {
		super(typeof(LabelsTests).simpleName)
	}
	
	@Test
	def test_UILabelProvider() {
		val entries = resourcesLoader.getResourceContents(CAR_DESCRIPTION)
		var findEntry = search.findEntry("(300)CAR", VCML_PACKAGE.class_, entries)
		assertNotNull(findEntry)
	
		var context = create(findEntry, null, Extract)
		var uiLabel = labels.getUILabel(context)
		assertEquals("Extract ", uiLabel)
			
		context = create(findEntry, VCML_PACKAGE.class_Characteristics, Extract)
		uiLabel = labels.getUILabel(context)
		assertEquals("Extract characteristics ", uiLabel)
		
		findEntry = search.findEntry("PRECOND", VCML_PACKAGE.precondition, entries)
		assertNotNull("precondition PRECOND not found", findEntry)
		context = create(findEntry, VCML_PACKAGE.VCObject_Description, Replace)
		uiLabel = context.label
		assertEquals("Replace description with a new value", uiLabel)
	}
}