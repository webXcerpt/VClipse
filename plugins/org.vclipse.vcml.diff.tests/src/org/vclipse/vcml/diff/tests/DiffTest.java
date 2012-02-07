package org.vclipse.vcml.diff.tests;

import static org.junit.Assert.assertTrue;

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipselabs.xtext.utils.unittesting.XtextRunner2;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner2.class)
@InjectWith(VcmlDiffInjectorProvider.class)
public class DiffTest extends XtextTest {

	public DiffTest() {
		super(DiffTest.class.getSimpleName());
	}
	
	@Test
	public void newSimpleCharacteristicTest() {
		ignoreSerializationDifferences();
		testFile("firstversion.vcml");
		testFile("secondversion.vcml");
		testFile("secondversion_diff.vcml");
		testExistingFiles("firstversion.vcml", "secondversion.vcml", "secondversion_diff.vcml");
	}

	protected void testExistingFiles(String firstVersion, String secondVersion, String resultVersion) {
		assertTrue(replaceBotheringStrings(loadFileContents(resourceRoot, secondVersion)).equals(
				replaceBotheringStrings(loadFileContents(resourceRoot, resultVersion), "import \"" + firstVersion + "\"")));
	}
	
	protected String replaceBotheringStrings(String targetString, String ... additionalReplacements) {
		for(String part : additionalReplacements) {
			targetString = targetString.replace(part, "");
		}
		return targetString.replace("\r", "").replace("\n", "").replace("\t", "").replace(" ", "").trim();
	}
}
