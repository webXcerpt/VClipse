package org.vclipse.vcml.diff.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.vclipse.vcml.diff.Comparison;

import com.google.inject.Inject;

public abstract class DiffTest extends XtextTest {

	protected Resource resource_compare_one;
	protected Resource resource_compare_two;
	protected Resource resource_existing_result;
	protected Resource resource_target_result;
	
	@Inject
	private Comparison comparison;
	
	@Inject
	private ISerializer serializer;
	
	public DiffTest(String resourceRoot) {
		super(resourceRoot == null ? DiffTest.class.getSimpleName() : resourceRoot);
	}
	
	public void test(String oldState, String newState, String diffExistingState, String targetReplacements) {
		resource_compare_one = loadAndSaveModule("", oldState).getSecond().getResource();
		resource_compare_two = loadAndSaveModule("", newState).getSecond().getResource();
		resource_existing_result = loadAndSaveModule("", diffExistingState).getSecond().getResource();
		
		String newStateResourceUri = resource_compare_two.getURI().toString();
		newStateResourceUri = newStateResourceUri.replace("cml2", "cml2_diff");
		Resource diffResource = resourceSet.createResource(URI.createURI(newStateResourceUri));
		
		try {
			comparison.compare(resource_compare_one, resource_compare_two, diffResource, new NullProgressMonitor());
		} catch (InterruptedException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		EList<EObject> contents = resource_existing_result.getContents();
		assertTrue(!contents.isEmpty());
		String diff_existing_model_serialized = serializer.serialize(contents.get(0));
		
		contents = diffResource.getContents();
		assertTrue(!contents.isEmpty());
		String diff_created_model_serialized = serializer.serialize(contents.get(0));
		
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
