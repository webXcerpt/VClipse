/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.tests.compare

import com.google.inject.Inject
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.refactoring.utils.EntrySearch
import org.vclipse.tests.VClipseTestUtilities
import org.vclipse.vcml.compare.VCMLCompareOperation
import org.vclipse.vcml.vcml.Characteristic
import org.vclipse.vcml.vcml.NumericType
import org.vclipse.vcml.vcml.SymbolicType
import org.vclipse.vcml.vcml.VcmlFactory
import org.vclipse.vcml.vcml.VcmlModel
import org.vclipse.vcml.vcml.VcmlPackage

import static org.junit.Assert.*

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(VCMLCompareInjectorProvider))
class VCMLCompareTests extends XtextTest {
	
	@Inject
	private VCMLCompareOperation compare
	
	@Inject
	private VClipseTestUtilities resources
	
	@Inject
	private EntrySearch entrySearch
	
	private NullProgressMonitor monitor
	
	private VcmlFactory vcmlFactory
	private VcmlPackage vcmlPackage
	
	
	/**
	 * Following tests are to execute as JUnit Plug-in tests
	 */
	 
	override before() {
		super.before()
		monitor = new NullProgressMonitor
		vcmlFactory = VcmlFactory::eINSTANCE
		vcmlPackage = VcmlPackage::eINSTANCE
	}
	
	/* if new vc objects are added to a vcml model, they should be extracted
	 * during the compare operation.
	 */
	@Test
	def testAddingVCObjects() {
		val vcml = resources.getResourceRoot("/compare/added_vc_objects/VCML/engine.vcml")
		val sap = resources.getResourceRoot("/compare/added_vc_objects/SAP/engine.vcml")
		
		val result = vcmlFactory.createVcmlModel
		compare.compare(sap as VcmlModel, vcml as VcmlModel, result, monitor)
		assertFalse(result.objects.empty)
		assertFalse(compare.reportedProblems)
	}
	
	/* type changes for existing characteristics are not allowed, the changed object 
	 * is extracted and a marker is created for the characteristic type 
	 */ 
	@Test
	def testChangedCsticType() {
		val vcml = resources.getResourceRoot("/compare/changed_cstic_type/VCML/car.vcml")
		val sap = resources.getResourceRoot("/compare/changed_cstic_type/SAP/car.vcml")

		var result = vcmlFactory.createVcmlModel
		compare.compare(sap as VcmlModel, vcml as VcmlModel, result, monitor)
		assertFalse(result.objects.empty)
		assertTrue(compare.reportedProblems)
		var entry = entrySearch.findEntry("NAME", vcmlPackage.characteristic, result.objects)
		assertFalse(entry == null)
		assertTrue((entry as Characteristic).type instanceof NumericType)
		assertTrue(result.objects.size == 1)
		
		// should be possible in both directions 
		result = vcmlFactory.createVcmlModel
		compare.compare(vcml as VcmlModel, sap as VcmlModel, result, monitor)
		assertFalse(result.objects.empty)
		assertTrue(compare.reportedProblems)
		entry = entrySearch.findEntry("NAME", vcmlPackage.characteristic, result.objects)
		assertFalse(entry == null)
		assertTrue("object: " + entry.eClass.name, (entry as Characteristic).type instanceof SymbolicType)
		assertTrue(result.objects.size == 1)
	}
}