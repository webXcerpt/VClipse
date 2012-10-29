package org.vclipse.refactoring.tests;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Lists;

public abstract class RefactoringTest extends XtextTest {

	protected static final String RESOURCES_PATH = "org.vclipse.refactoring.tests/resources/";
	
	protected static final VcmlPackage VCML = VcmlPackage.eINSTANCE;
	
	protected RefactoringTest(String testname) {
		super(testname);
	}
	
	protected void loadContents(String filename, List<EObject> contents) {
		String path = RESOURCES_PATH + filename;
		URI uri = URI.createPlatformPluginURI(path, true);
		Resource resource = resourceSet.getResource(uri, true);
		EList<EObject> resourceContents = resource.getContents();
		if(!resourceContents.isEmpty()) {
			EObject object = resourceContents.get(0);
			contents.add(object);
			List<EObject> entries = Lists.newArrayList(object.eAllContents());
			contents.addAll(entries);
		}
	}
}
