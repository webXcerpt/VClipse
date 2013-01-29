/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator(www.webXcerpt.com)
 ******************************************************************************/
package org.vclipse.tests.compare

import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.eclipse.xtext.junit4.XtextRunner

import org.eclipse.xtext.junit4.InjectWith
import org.junit.runner.RunWith
import org.junit.Test
import com.google.inject.Inject

import org.vclipse.vcml.compare.VcmlCompareOperation
import org.vclipse.tests.VClipseTestResourceLoader
import org.vclipse.vcml.vcml.VcmlFactory
import org.eclipse.core.runtime.NullProgressMonitor
import org.vclipse.vcml.vcml.VcmlModel

import static org.junit.Assert.*

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CompareInjectorProvider))
class CompareTestNewObjectsAdded extends XtextTest {
	
	@Inject
	private VcmlCompareOperation compare
	
	@Inject
	private VClipseTestResourceLoader resources
	
	/**
	 * Following tests are to execute as JUnit Plug-in tests
	 */
	
	@Test
	def testAddingVCObjects() {
		val vcml = resources.getResourceRoot("/compare/added_vc_objects/VCML/engine.vcml")
		val sap = resources.getResourceRoot("/compare/added_vc_objects/SAP/engine.vcml")
		
		val monitor = new NullProgressMonitor
		val result = VcmlFactory::eINSTANCE.createVcmlModel
		
		compare.compare(sap as VcmlModel, vcml as VcmlModel, result, monitor)
		
		// assertFalse(result.imports.empty) disabled -> model level -> no imports
		assertFalse(result.objects.empty)
		assertFalse(compare.reportedProblems)
	}
}