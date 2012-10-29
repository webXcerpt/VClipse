package org.vclipse.refactoring.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.refactoring.core.RefactoringContext;
import org.vclipse.refactoring.core.RefactoringType;
import org.vclipse.refactoring.utils.Configuration;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.refactoring.utils.Extensions;
import org.vclipse.refactoring.utils.Labels;
import org.vclipse.vcml.VCMLInjectorProvider;

import com.google.common.collect.Lists;

@RunWith(XtextRunner.class)
@InjectWith(VCMLInjectorProvider.class)
public class LabelsTests extends RefactoringTest {

	private Labels labels;
	private EntrySearch search;
	
	public LabelsTests() {
		super(LabelsTests.class.getName());
		Configuration configuration = new Configuration();
		Extensions extensions = new Extensions(configuration);
		labels = new Labels(extensions);
		search = new EntrySearch(extensions);
	}
	
	@Test
	public void testUILabelProvider() {
		List<EObject> contents = Lists.newArrayList();
		loadContents("car_description.vcml", contents);
		EObject findEntry = search.findEntry("(300)CAR", VCML.getClass_(), contents);
		assertNotNull(findEntry);
		
		RefactoringContext context = RefactoringContext.create(findEntry, null, RefactoringType.Extract);
		String uiLabel = labels.getUILabel(context);
		assertEquals("Extract ", uiLabel);
		
		context = RefactoringContext.create(findEntry, VCML.getClass_Characteristics(), RefactoringType.Extract);
		uiLabel = labels.getUILabel(context);
		assertEquals("Extract characteristics ", uiLabel);
	}
}
