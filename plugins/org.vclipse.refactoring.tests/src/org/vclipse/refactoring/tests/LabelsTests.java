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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.refactoring.core.RefactoringContext;
import org.vclipse.refactoring.core.RefactoringType;
import org.vclipse.vcml.VCMLInjectorProvider;
import org.vclipse.vcml.vcml.VcmlPackage;

@RunWith(XtextRunner.class)
@InjectWith(VCMLInjectorProvider.class)
public class LabelsTests extends RefactoringTest {

	protected static final VcmlPackage VCML = VcmlPackage.eINSTANCE;
	
	public LabelsTests() {
		super(LabelsTests.class.getName());
	}
	
	@Test
	public void testUILabelProvider() {
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
