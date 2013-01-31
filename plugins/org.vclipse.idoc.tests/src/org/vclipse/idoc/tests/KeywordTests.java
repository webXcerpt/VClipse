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
package org.vclipse.idoc.tests;

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.idoc.IDocInjectorProvider;

@RunWith(XtextRunner.class)
@InjectWith(IDocInjectorProvider.class)
public class KeywordTests extends XtextTest {

	@Test
	public void keywordsTests() {
		testKeyword("idoc");
		testKeyword("segment");
		testKeyword("import");
		testKeyword("{");
		testKeyword("}");
		testKeyword("=");
		
		testTerminal("0", "INT");
		testTerminal("100", "INT");
		
		testTerminal("/testid", "ID");
		testTerminal("/////testid", "ID");
		
		testNotTerminal("\\", "ANY_OTHER");
		
		testNotTerminal("-100", "INT");
	}
}
