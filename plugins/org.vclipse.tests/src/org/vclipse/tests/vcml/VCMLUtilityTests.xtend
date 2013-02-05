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
package org.vclipse.tests.vcml

import com.google.inject.Inject
import junit.framework.Assert
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.serializer.ISerializer
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.tests.VClipseTestPlugin
import org.vclipse.tests.VClipseTestUtilities
import org.vclipse.vcml.VCMLUtilities
import org.vclipse.vcml.vcml.VCObject
import org.vclipse.vcml.vcml.VcmlModel
import org.vclipse.vcml.vcml.VcmlPackage
import org.vclipse.base.naming.INameProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(VClipseTestPlugin))
class VCMLUtilityTests extends XtextTest {
	
	@Inject
	private VClipseTestUtilities testUtilities
	
	@Inject
	private VCMLUtilities vcmlUtility
	
	@Inject
	private ISerializer vcmlSerializer
	
	@Inject
	private INameProvider nameProvider
	
	private VcmlPackage vcmlPackage = VcmlPackage::eINSTANCE
	
	new() {
		super(
			typeof(VCMLUtilityTests).simpleName
		)
	}
	
	@Test
	def void test_SortVCObjectsList() {
		val car_vcml = testUtilities.getResource("/compare/added_vc_objects/VCML/car.vcml")
		val vcml_model = car_vcml.contents.get(0) as VcmlModel
		
		val contens_prior_sort = testUtilities.removeNoise(vcmlSerializer.serialize(vcml_model))
		vcmlUtility.sortEntries(vcml_model.objects, [
			VCObject first, VCObject second |
				first.name.compareTo(second.name)
			])
		val contents_after_sort = testUtilities.removeNoise(vcmlSerializer.serialize(vcml_model))
		Assert::assertFalse("Sort algorithm does not have an effect.", contens_prior_sort.equals(contents_after_sort))
	}
	
	@Test
	def void test_FindEntries() {
		val car_vcml = testUtilities.getResource("/compare/added_vc_objects/VCML/car.vcml")
		val vcml_model = car_vcml.contents.get(0) as VcmlModel
		
		var found = vcmlUtility.findEntry("(300)CAR", vcmlPackage.class_, vcml_model.objects, nameProvider)
		Assert::assertNotNull("Entry is existing an was found.", found)
		
		found = vcmlUtility.findEntry("NAME", vcmlPackage.characteristic, vcml_model.objects, nameProvider)
		Assert::assertNotNull("Entry is existing an was found.", found)
		
		found = vcmlUtility.findEntry("NAME2", vcmlPackage.characteristic, vcml_model.objects, nameProvider)
		Assert::assertNull("Entry does not exist.", found)
		
		found = vcmlUtility.findEntry("NAME", vcmlPackage.constraint, vcml_model.objects, nameProvider)
		Assert::assertNull("Entry does not exist.", found)
	}
}