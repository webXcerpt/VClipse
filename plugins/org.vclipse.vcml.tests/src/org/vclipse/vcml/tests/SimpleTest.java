package org.vclipse.vcml.tests;

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.vclipse.vcml.VCMLInjectorProvider;

@RunWith(XtextRunner.class)
@InjectWith(VCMLInjectorProvider.class)
public class SimpleTest extends XtextTest {
	
	public SimpleTest() {
		super(SimpleTest.class.getSimpleName());
	}

	@Test
	public void simpleFileTest() {
		//suppressSerialization(); // currently, serialization leads to an error (NPE) -> VCMLSerializer -> usePrettyPrinter() -> Platform is not available during the test.
		testFile("simpletest.vcml");
	}

}
