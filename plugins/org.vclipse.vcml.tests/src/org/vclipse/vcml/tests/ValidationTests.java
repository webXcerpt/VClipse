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
