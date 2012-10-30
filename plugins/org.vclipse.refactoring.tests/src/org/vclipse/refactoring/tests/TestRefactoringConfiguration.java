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
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.refactoring.IRefactoringConfiguration;
import org.vclipse.refactoring.core.RefactoringContext;
import org.vclipse.refactoring.core.RefactoringType;
import org.vclipse.vcml.VCMLInjectorProvider;

import com.webxcerpt.cm.nsn.cml.cml.CmlFactory;
import com.webxcerpt.cm.nsn.cml.cml.CmlPackage;
import com.webxcerpt.cm.nsn.cml.cml.Variable;

@RunWith(XtextRunner.class)
@InjectWith(VCMLInjectorProvider.class)
public class TestRefactoringConfiguration extends RefactoringTest {
	
	private CmlFactory cmlFactory;
	private CmlPackage cmlPackage;
	
	public TestRefactoringConfiguration() {
		super(TestRefactoringConfiguration.class.getName());
		cmlFactory = CmlFactory.eINSTANCE;
		cmlPackage = CmlPackage.eINSTANCE;
	}

	@Test
	public void testRefactoringConfiguration() {
		Variable variable = CmlFactory.eINSTANCE.createVariable();
		EObject eobject = contents.get(contents.size() / 2);
		IRefactoringConfiguration configuration = extensions.getInstance(IRefactoringConfiguration.class, eobject);
		RefactoringContext context = RefactoringContext.create(variable, cmlPackage.getItem_Statements(), RefactoringType.Inline);
		boolean initialize = configuration.initialize(context);
		Assert.assertEquals("context initialized", true, initialize);
		
		List<? extends EStructuralFeature> features = configuration.provideFeatures(context);
		Assert.assertTrue(!features.isEmpty());
		Assert.assertTrue(features.contains(cmlPackage.getItem_Statements()));
	}
}
