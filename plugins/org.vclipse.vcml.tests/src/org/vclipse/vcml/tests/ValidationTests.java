/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.vcml.tests;

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.vcml.VCMLInjectorProvider;

import org.eclipse.xtext.junit4.XtextRunner;

@RunWith(XtextRunner.class)
@InjectWith(VCMLInjectorProvider.class)
public class ValidationTests extends XtextTest {

	public ValidationTests() {
		super(ValidationTests.class.getSimpleName());
	}
	
	@Test
	public void testConstraintValidation_NotRestrictedInferences() {
		testFile("ticket.vcml");
	}
}
