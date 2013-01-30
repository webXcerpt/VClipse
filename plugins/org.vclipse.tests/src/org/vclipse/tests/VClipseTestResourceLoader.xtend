/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.tests

import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.EcoreUtil2
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.vclipse.vcml.vcml.VcmlFactory
import org.vclipse.vcml.vcml.VcmlPackage

import static com.google.common.collect.Lists.*
import static org.eclipse.emf.common.util.URI.*
import static org.vclipse.tests.VClipseTestResourceLoader.*

/*
 * 
 */
class VClipseTestResourceLoader extends XtextTest {
	
	public static String PREFIX = "/CarDescription/"
	public static String DEPENDENCIES_PREFIX = PREFIX + "car_description-dep/"
	
	public static String CAR_DESCRIPTION = PREFIX + "car_description.vcml"
	
	public static String DEPENDENCY_CONS = DEPENDENCIES_PREFIX + "CS_CAR1.cons"
	public static String DEPENDENCY_PRE = DEPENDENCIES_PREFIX + "PRECOND.pre"
	public static String DEPENDENCY_PROC = DEPENDENCIES_PREFIX + "PROC.proc"
	public static String DEPENDENCY_SEL = DEPENDENCIES_PREFIX + "SEL_COND.sel"
 
	public static VcmlPackage VCML_PACKAGE = VcmlPackage::eINSTANCE
	public static VcmlFactory VCML_FACTORY = VcmlFactory::eINSTANCE
	
	/**
	 * 
	 */
	def getAllEntries(EObject entry) {
		val rootContainer = EcoreUtil2::getRootContainer(entry)
		val entries = newArrayList(rootContainer.eAllContents)
		entries.add(0, rootContainer)
		entries
	}
	
	/**
	 * 
	 */
 	def getResource(String location) {
 		getResourceRoot(location).eResource
 	}
 	
 	/*
 	 * Returns the top level element from the resource lying on the location.
 	 */
	def getResourceRoot(String location) {
		val uri = createPlatformPluginURI(VClipseTestPlugin::ID + location, true)
		val resource = resourceSet.getResource(uri, true)
		val contents = resource.contents
		if(contents.empty) {
			return null
		} 
		return contents.get(0)
	}
	
	/**
	 * 
	 */
	def getInputStream(String location) {
		getClass().classLoader.getResourceAsStream(location)
	}
	
	/**
	 * 
	 */
	def getResourceContents(String location) {
		val root = getResourceRoot(location)
		if(root == null) {
			return <EObject>newArrayList
		} 
		val contents = <EObject>newArrayList(root.eAllContents)
		contents.add(0, root)
		return contents
	}
}