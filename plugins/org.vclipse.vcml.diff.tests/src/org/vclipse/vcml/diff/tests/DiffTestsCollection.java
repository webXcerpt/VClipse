package org.vclipse.vcml.diff.tests;

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipselabs.xtext.utils.unittesting.XtextRunner2;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner2.class)
@InjectWith(VcmlDiffInjectorProvider.class)
public class DiffTestsCollection extends DiffTest {

	public DiffTestsCollection() {
		super(DiffTestsCollection.class.getSimpleName());
	}
	
	@Test
	public void newSimpleCharacteristicTest() {
		ignoreSerializationDifferences();
		testFile("firstversion.vcml");
		testFile("secondversion.vcml");
		
		test("firstversion.vcml", "secondversion.vcml", "target_result.vcml", "import \"firstversion.vcml\"");
	}
}
