package org.vclipse.idoc.tests;

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.vclipse.idoc.IDocInjectorProvider;

@RunWith(XtextRunner.class)
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
