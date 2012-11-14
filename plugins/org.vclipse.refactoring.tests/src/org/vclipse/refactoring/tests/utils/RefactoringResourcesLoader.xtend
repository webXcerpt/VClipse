package org.vclipse.refactoring.tests.utils

import org.eclipse.emf.ecore.EObject
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.vclipse.vcml.vcml.VcmlFactory
import org.vclipse.vcml.vcml.VcmlPackage

import static com.google.common.collect.Lists.*
import static org.eclipse.emf.common.util.URI.*
import static org.junit.Assert.*
import static org.vclipse.refactoring.RefactoringPlugin.*
import static org.vclipse.refactoring.tests.utils.RefactoringResourcesLoader.*
import org.eclipse.xtext.EcoreUtil2

class RefactoringResourcesLoader extends XtextTest {
	
	public static String PREFIX = "/CarDescription/"
	public static String DEPENDENCIES_PREFIX = PREFIX + "car_description-dep/"
	
	public static String CAR_DESCRIPTION = PREFIX + "car_description.vcml"
	
	public static String DEPENDENCY_CONS = DEPENDENCIES_PREFIX + "CS_CAR1.cons"
	public static String DEPENDENCY_PRE = DEPENDENCIES_PREFIX + "PRECOND.pre"
	public static String DEPENDENCY_PROC = DEPENDENCIES_PREFIX + "PROC.proc"
	public static String DEPENDENCY_SEL = DEPENDENCIES_PREFIX + "SEL_COND.sel"
 
	public static VcmlPackage VCML_PACKAGE = VcmlPackage::eINSTANCE
	public static VcmlFactory VCML_FACTORY = VcmlFactory::eINSTANCE
	
	def getAllEntries(EObject entry) {
		val rootContainer = EcoreUtil2::getRootContainer(entry)
		val entries = newArrayList(rootContainer.eAllContents)
		entries.add(0, rootContainer)
		entries
	}
	
 	def getResource(String path) {
 		getResourceRoot(path).eResource
 	}
 	
	def getResourceRoot(String path) {
		val uri = createPlatformPluginURI(ID + path, true)
		val resource = resourceSet.getResource(uri, true)
		val contents = resource.contents
		if(contents.empty) {
			return null
		} else {
			contents.get(0)
		}
	}

	def getResourceContents(String path) {
		val root = getResourceRoot(path)
		if(root == null) {
			return <EObject>newArrayList
		} else {
			val contents = <EObject>newArrayList(root.eAllContents)
			contents.add(0, root)
			return contents
		}
	}
	
	def <T> assertNotEmpty(Iterable<T> entries) {
		if(entries.empty) {
			fail("no entries in the iterable.")
		}
	}
}