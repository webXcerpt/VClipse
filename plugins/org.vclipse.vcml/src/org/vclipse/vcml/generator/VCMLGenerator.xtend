/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.generator

import com.google.inject.Inject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IGenerator

import static org.vclipse.vcml.generator.VCMLGenerator.*

abstract class VCMLGenerator implements IGenerator {

	private static String VCML_FILE_EXTENSION = "vcml";	
	
	@Inject
	private VCMLOutputConfigurationProvider configurationProvider
 
	def getVcmlUri(Resource resource) {
		var uri = resource.URI
		uri = uri.trimFileExtension.appendFileExtension(VCML_FILE_EXTENSION)
		val foldername = configurationProvider.compileFolderName
		return uri.trimSegments(1).appendSegment(foldername).appendSegment(uri.lastSegment)
	}
}