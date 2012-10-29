package org.vclipse.refactoring.tests;

import java.util.List;

import junit.framework.Assert;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.refactoring.utils.Configuration;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.refactoring.utils.Extensions;
import org.vclipse.vcml.VCMLInjectorProvider;

import com.google.common.collect.Lists;

@RunWith(XtextRunner.class)
@InjectWith(VCMLInjectorProvider.class)
public class SearchTest extends RefactoringTest {

	private EntrySearch search;
	
	public SearchTest() {
		super(SearchTest.class.getName());
		Configuration configuration = new Configuration();
		Extensions extensions = new Extensions(configuration);
		search = new EntrySearch(extensions);
	}

	@Test
	public void testFindObject() {
		List<EObject> contents = Lists.newArrayList();
		loadContents("car_description.vcml", contents);
		Assert.assertTrue(!contents.isEmpty());
		int size = contents.size();
		EObject entry = contents.get((size / 2) + 1);
		EObject findEntry = search.findEntry(entry, contents);
		Assert.assertNotNull(findEntry);
		
		EObject jft = EcoreFactory.eINSTANCE.createEObject();
		findEntry = search.findEntry(jft, contents);
		Assert.assertNull(findEntry);
	}
	
	@Test
	public void testFindByTypeAndName() {
		List<EObject> contents = Lists.newArrayList();
		loadContents("car_description.vcml", contents);
		Assert.assertTrue(!contents.isEmpty());
		EObject findEntry = search.findEntry("SEL_COND", VCML.getSelectionCondition(), contents);
		Assert.assertNotNull(findEntry);
		findEntry = search.findEntry("SEL_COND", VCML.getCharacteristic(), contents);
		Assert.assertNotNull(findEntry);
		findEntry = search.findEntry("(300)CAR", VCML.getClass_(), contents);
		Assert.assertNotNull(findEntry);
		findEntry = search.findEntry("ENGINE_2400", VCML.getMaterial(), contents);
		Assert.assertNotNull(findEntry);
		findEntry = search.findEntry("ENGINE_2400", VCML.getCharacteristic(), contents);
		Assert.assertNull(findEntry);
		findEntry = search.findEntry("FUELCONSUMPTION", VCML.getMaterial(), contents);
		Assert.assertNull(findEntry);
	}
	
	@Test
	public void testSearchByName() {
		List<EObject> contents = Lists.newArrayList();
		loadContents("car_description.vcml", contents);
		Assert.assertTrue(!contents.isEmpty());
		Iterable<EObject> namedIterable = search.getEntries("DEP_NET", contents);
		Assert.assertNotNull(namedIterable);
		List<EObject> namedEntries = Lists.newArrayList(namedIterable);
		Assert.assertEquals(1, namedEntries.size());
	}

	@Test
	public void testSearchByType() {
		List<EObject> contents = Lists.newArrayList();
		loadContents("car_description.vcml", contents);
		List<EObject> classes = Lists.newArrayList(search.getEntries(VCML.getClass_(), contents));
		Assert.assertEquals("Amount of classes", 1, classes.size());
		List<EObject> cstics = Lists.newArrayList(search.getEntries(VCML.getCharacteristic(), contents));
		Assert.assertEquals("Amount of cstics", 9, cstics.size());
	}
}
