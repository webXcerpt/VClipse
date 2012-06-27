package org.vclipse.idoc.tests;

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipselabs.xtext.utils.unittesting.XtextRunner2;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.idoc.IDocInjectorProvider;

@RunWith(XtextRunner2.class)
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
