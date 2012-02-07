package org.vclipse.vcml.diff.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
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
	
	public void test(String compare_one, String compare_two, String existing_result, String targetResult, String targetReplacements) {
		resource_compare_one = loadAndSaveModule("", compare_one).getSecond().getResource();
		resource_compare_two = loadAndSaveModule("", compare_two).getSecond().getResource();
		resource_existing_result = loadAndSaveModule("", existing_result).getSecond().getResource();
		resource_target_result = loadAndSaveModule("", targetResult).getSecond().getResource();
		
		try {
			comparison.compare(resource_compare_one, resource_compare_two, resource_existing_result, new NullProgressMonitor());
		} catch (InterruptedException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		EList<EObject> contents = resource_existing_result.getContents();
		assertTrue(!contents.isEmpty());
		String existing_result_string = serializer.serialize(contents.get(0));
		
		contents = resource_target_result.getContents();
		assertTrue(!contents.isEmpty());
		String target_result_string = serializer.serialize(contents.get(0));
		
		String existing_not_bothered = replaceBotheringStrings(existing_result_string, targetReplacements);
		String target_not_bothered = replaceBotheringStrings(target_result_string, targetReplacements);
		
		assertTrue(existing_not_bothered.equals(target_not_bothered));
	}
	
	public String replaceBotheringStrings(String targetString, String ... additionalReplacements) {
		for(String part : additionalReplacements) {
			targetString = targetString.replace(part, "");
		}
		return targetString.replace("\r", "").replace("\n", "").replace("\t", "").replace(" ", "").trim();
	}
}
