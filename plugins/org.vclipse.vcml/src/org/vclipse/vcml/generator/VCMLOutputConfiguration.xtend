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