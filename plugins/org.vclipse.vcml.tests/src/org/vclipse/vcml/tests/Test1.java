package org.vclipse.vcml.tests;

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipselabs.xtext.utils.unittesting.XtextRunner2;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.vclipse.vcml.VCMLInjectorProvider;

@RunWith(XtextRunner2.class)
@InjectWith(VCMLInjectorProvider.class)
public class Test1 extends XtextTest {

	public Test1() {
		super("Test1");
	}

	@Test
	public void test1() {
		testFile("test1.vcml");
	}

}
