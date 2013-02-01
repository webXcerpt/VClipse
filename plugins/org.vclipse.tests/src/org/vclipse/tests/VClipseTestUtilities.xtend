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
package org.vclipse.tests

import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.EcoreUtil2
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.vclipse.vcml.vcml.VcmlFactory
import org.vclipse.vcml.vcml.VcmlPackage

import static com.google.common.collect.Lists.*
import static org.eclipse.emf.common.util.URI.*
import static org.vclipse.tests.VClipseTestUtilities.*

/**
 * Utilities for VClipse tests.
 */
class VClipseTestUtilities extends XtextTest {
	
	public static VcmlPackage VCML_PACKAGE = VcmlPackage::eINSTANCE
	public static VcmlFactory VCML_FACTORY = VcmlFactory::eINSTANCE
	
	/**
	 * Returns all contents of an entries container.
	 */
	def getAllEntries(EObject entry) {
		val rootContainer = EcoreUtil2::getRootContainer(entry)
		val entries = newArrayList(rootContainer.eAllContents)
		entries.add(0, rootContainer)
		entries
	}
	
	/**
	 * Loads a resource for a particular location.
	 */
 	def getResource(String location) {
 		getResourceRoot(location).eResource
 	}
 	
 	/*
 	 * Returns the top level element for a resource on particular location.
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
	 * Provides an input stream for a particular location.
	 */
	def getInputStream(String location) {
		getClass().classLoader.getResourceAsStream(location)
	}
	
	/**
	 * Loads all contents of a resource on a particular location.
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
	
	/**
	 * Removes new lines, tabulators, white spaces and values in the remove argument from the string.
	 */
	def String removeNoise(String string, String ... remove) {
		var output = if(remove.empty) string  else  ""
		for(part : remove) {
			output = string.replace(part, "")
		}
		return output.replace("\r", "").replace("\n", "").replace("\t", "").replace(" ", "").trim()
	}
}