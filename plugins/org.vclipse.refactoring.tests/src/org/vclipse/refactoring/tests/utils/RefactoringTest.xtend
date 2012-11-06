package org.vclipse.refactoring.tests.utils

import com.google.common.collect.Lists
import java.util.List
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.vclipse.refactoring.RefactoringPlugin
import org.vclipse.vcml.vcml.VcmlFactory
import org.vclipse.vcml.vcml.VcmlPackage

class RefactoringTest extends XtextTest {
	
	protected String CAR_DESCRIPTION = "/CarDescription/car_description.vcml"
	
	protected VcmlPackage VCML_PACKAGE = VcmlPackage::eINSTANCE
	protected VcmlFactory VCML_FACTORY = VcmlFactory::eINSTANCE
	
	protected List<EObject> entries
	
	new(String resourceRoot) {
		super(resourceRoot)
	}
 
 	override before() {
 		super.before
 		entries = getResourceContents(CAR_DESCRIPTION)
 	}
 
	def getResourceRoot(String path) {
		val uri = URI::createPlatformPluginURI(RefactoringPlugin::ID + path, true);
		val resource = resourceSet.getResource(uri, true);
		val contents = resource.getContents();
		if(contents.empty) {
			return null
		} else {
			contents.get(0)
		}
	}

	def getResourceContents(String path) {
		val root = getResourceRoot(path)
		if(root == null) {
			return Lists::<EObject>newArrayList
		} else {
			val contents = Lists::<EObject>newArrayList(root.eAllContents)
			contents.add(0, root)
			return contents
		}
	}
}