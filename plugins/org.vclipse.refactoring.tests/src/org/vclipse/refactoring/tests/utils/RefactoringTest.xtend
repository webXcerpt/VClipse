package org.vclipse.refactoring.tests.utils

import java.util.List
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Assert
import org.junit.runner.RunWith
import org.vclipse.refactoring.RefactoringPlugin
import com.google.common.collect.Lists
import org.vclipse.vcml.vcml.VcmlPackage
import org.vclipse.vcml.vcml.VcmlFactory

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(RefactoringTestInjectorProvider))
public class RefactoringTest extends XtextTest {
	
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

	def assertEquals(String message, EObject object_one, EObject object_two) {
		Assert::assertTrue(
			"\n" + " Expected " + object_one + "\n"  + " Existing " + object_two + "\n", 
				EcoreUtil::equals(object_one, object_two))
	}
	 
	def String removeNoise(String string, List<String> remove) {
		var output = if(remove.empty) string  else  ""
		for(part : remove) {
			output = string.replace(part, "")
		}
		return output.replace("\r", "").replace("\n", "").replace("\t", "").replace(" ", "").trim()
	}
}