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

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.vcml.VCMLInjectorProvider;

@RunWith(XtextRunner.class)
@InjectWith(VCMLInjectorProvider.class)
public class ReferencesSearchTests extends RefactoringTest {

	public ReferencesSearchTests() {
		super(ReferencesSearchTests.class.getName());
	}
	
	@Test
	public void testReferencesSearch() {
		
	}
}
