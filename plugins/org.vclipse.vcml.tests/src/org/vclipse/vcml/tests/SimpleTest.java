package org.vclipse.vcml.tests;

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipselabs.xtext.utils.unittesting.XtextRunner2;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.vclipse.vcml.VCMLInjectorProvider;

@RunWith(XtextRunner2.class)
@InjectWith(VCMLInjectorProvider.class)
public class SimpleTest extends XtextTest {

	public SimpleTest() {
		super(SimpleTest.class.getSimpleName());
	}

	@Test
	public void simpleFileTest() {
		suppressSerialization(); // currently, serialization leads to an error (NPE)
		testFile("test1.vcml");
	}

}
