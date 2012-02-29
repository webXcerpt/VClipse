package org.vclipse.vcml.diff.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.vclipse.vcml.diff.Comparison;
import org.vclipse.vcml.formatting.VCMLPrettyPrinter;

import com.google.inject.Inject;

public abstract class DiffTest extends XtextTest {

	protected Resource oldResource;
	protected Resource newResource;
	protected Resource diffExistingResource;
	protected Resource resource_target_result;
	
	@Inject
	private Comparison comparison;
	
	private VCMLPrettyPrinter prettyPrinter;
	
	public DiffTest(String resourceRoot) {
		super(resourceRoot == null ? DiffTest.class.getSimpleName() : resourceRoot);
		prettyPrinter = new VCMLPrettyPrinter();
	}
	
	public void test(String oldState, String newState, String diffExistingState, String targetReplacements) {
		oldResource = loadAndSaveModule("", oldState).getSecond().getResource();
		newResource = loadAndSaveModule("", newState).getSecond().getResource();
		diffExistingResource = loadAndSaveModule("", diffExistingState).getSecond().getResource();
		
		String newStateResourceUri = newResource.getURI().toString();
		newStateResourceUri = newStateResourceUri.replace(".vcml", "_diff.vcml");
		Resource diffResource = resourceSet.createResource(URI.createURI(newStateResourceUri));
		
		try {
			comparison.compare(oldResource, newResource, diffResource, new NullProgressMonitor());
		} catch (InterruptedException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		EList<EObject> contents = diffExistingResource.getContents();
		assertTrue(!contents.isEmpty());
		String diff_existing_model_serialized = prettyPrinter.prettyPrint(contents.get(0));
		
		contents = diffResource.getContents();
		assertTrue(!contents.isEmpty());
		String diff_created_model_serialized = prettyPrinter.prettyPrint(contents.get(0));
		
		String diff_existing = replaceStrings(diff_existing_model_serialized, targetReplacements);
		String diff_created = replaceStrings(diff_created_model_serialized, targetReplacements);
		
		assertTrue(diff_existing.equals(diff_created));
	}
	
	public String replaceStrings(String string, String ... replacements) {
		for(String part : replacements) {
			string = string.replace(part, "");
		}
		return string.replace("\r", "").replace("\n", "").replace("\t", "").replace(" ", "").trim();
	}
}
