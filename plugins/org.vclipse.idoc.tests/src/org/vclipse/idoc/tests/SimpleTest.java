package org.vclipse.idoc.tests;

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipselabs.xtext.utils.unittesting.XtextRunner2;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.vclipse.idoc.IDocInjectorProvider;

@RunWith(XtextRunner2.class)
@InjectWith(IDocInjectorProvider.class)
public class SimpleTest extends XtextTest {

	public SimpleTest() {
		super(SimpleTest.class.getSimpleName());
	}

	@Test
	public void simpleFileTest() {
		ignoreSerializationDifferences();
		testFile("test1.idoc");
	}

}
