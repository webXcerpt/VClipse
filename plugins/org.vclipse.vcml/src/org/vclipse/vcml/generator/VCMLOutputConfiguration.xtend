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

import org.eclipse.xtext.generator.OutputConfiguration
import org.eclipse.xtext.generator.OutputConfigurationProvider

class VCMLOutputConfigurationProvider extends OutputConfigurationProvider {
	
	public static String VCML_OUTPUT = "COMPILE_OUTPUT";
	  
	override getOutputConfigurations() {
		val compileOutput = new OutputConfiguration(VCML_OUTPUT);
		compileOutput.setDescription("Generated VCML Resources");
		compileOutput.setOutputDirectory("./VCML");
		compileOutput.setOverrideExistingResources(true);
		compileOutput.setCreateOutputDirectory(true);
		compileOutput.setCleanUpDerivedResources(true);
		// VCML files must not be derived
		compileOutput.setSetDerivedProperty(false); 
		return newHashSet(compileOutput);
	}
	
	def getCompileOutput() {
		// we assume the vcml configuration is the first one
		return getOutputConfigurations().iterator().next();
	}
	
	def getCompileFolderName() {
		return getCompileOutput().getOutputDirectory().replace("./", "");
	}
}