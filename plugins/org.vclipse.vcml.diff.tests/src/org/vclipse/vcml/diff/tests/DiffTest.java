package org.vclipse.vcml.diff.tests;

import static org.junit.Assert.assertTrue;

import org.eclipselabs.xtext.utils.unittesting.XtextTest;

public abstract class DiffTest extends XtextTest {

	public DiffTest(String resourceRoot) {
		super(resourceRoot == null ? DiffTest.class.getSimpleName() : resourceRoot);
	}
	
	public void testExistingFiles(String firstVersion, String secondVersion, String resultVersion) {
		assertTrue(replaceBotheringStrings(loadFileContents(resourceRoot, secondVersion)).equals(
				replaceBotheringStrings(loadFileContents(resourceRoot, resultVersion), "import \"" + firstVersion + "\"")));
	}
	
	public String replaceBotheringStrings(String targetString, String ... additionalReplacements) {
		for(String part : additionalReplacements) {
			targetString = targetString.replace(part, "");
		}
		return targetString.replace("\r", "").replace("\n", "").replace("\t", "").replace(" ", "").trim();
	}
}
