/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.refactoring.tests;

import java.util.List;

import junit.framework.Assert;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.vcml.VCMLInjectorProvider;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Lists;

@RunWith(XtextRunner.class)
@InjectWith(VCMLInjectorProvider.class)
public class SearchTest extends RefactoringTest {

	protected static final VcmlPackage VCML = VcmlPackage.eINSTANCE;
	
	private EntrySearch search;
	
	public SearchTest() {
		super(SearchTest.class.getName());
	}

	@Test
	public void testFindObject() {
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
		Assert.assertTrue(!contents.isEmpty());
		Iterable<EObject> namedIterable = search.getEntries("DEP_NET", contents);
		Assert.assertNotNull(namedIterable);
		List<EObject> namedEntries = Lists.newArrayList(namedIterable);
		Assert.assertEquals(1, namedEntries.size());
	}

	@Test
	public void testSearchByType() {
		List<EObject> classes = Lists.newArrayList(search.getEntries(VCML.getClass_(), contents));
		Assert.assertEquals("Amount of classes", 1, classes.size());
		List<EObject> cstics = Lists.newArrayList(search.getEntries(VCML.getCharacteristic(), contents));
		Assert.assertEquals("Amount of cstics", 9, cstics.size());
	}
}
