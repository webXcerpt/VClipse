package org.vclipse.refactoring.tests

import org.eclipse.xtext.junit4.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.InjectWith
import org.junit.Assert
import org.vclipse.refactoring.tests.utils.RefactoringTestInjectorProvider
import org.vclipse.refactoring.tests.utils.RefactoringTest
import org.vclipse.refactoring.core.RefactoringContext
import org.vclipse.refactoring.core.RefactoringType
import com.google.inject.Inject
import org.vclipse.refactoring.utils.EntrySearch
import org.vclipse.refactoring.utils.Labels
import org.junit.Test

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(RefactoringTestInjectorProvider))
class LabelsTests extends RefactoringTest {
	
	@Inject
	private EntrySearch search;
	
	@Inject
	private Labels labels;
	
	new() {
		super(typeof(LabelsTests).simpleName)
	}
	
	@Test
	def test_UILabelProvider() {
		val findEntry = search.findEntry("(300)CAR", VCML_PACKAGE.getClass_(), entries);
		Assert::assertNotNull(findEntry);
	
		var context = RefactoringContext::create(findEntry, null, RefactoringType::Extract);
		var uiLabel = labels.getUILabel(context);
		Assert::assertEquals("Extract ", uiLabel);
			
		context = RefactoringContext::create(findEntry, VCML_PACKAGE.getClass_Characteristics(), RefactoringType::Extract);
		uiLabel = labels.getUILabel(context);
		Assert::assertEquals("Extract characteristics ", uiLabel);
	}
}