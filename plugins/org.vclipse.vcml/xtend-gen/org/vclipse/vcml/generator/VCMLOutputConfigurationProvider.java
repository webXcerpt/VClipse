package org.vclipse.vcml.generator;

import java.util.Iterator;
import java.util.Set;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.generator.OutputConfigurationProvider;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

@SuppressWarnings("all")
public class VCMLOutputConfigurationProvider extends OutputConfigurationProvider {
  public static String VCML_OUTPUT = "COMPILE_OUTPUT";
  
  public Set<OutputConfiguration> getOutputConfigurations() {
    OutputConfiguration _outputConfiguration = new OutputConfiguration(VCMLOutputConfigurationProvider.VCML_OUTPUT);
    final OutputConfiguration compileOutput = _outputConfiguration;
    compileOutput.setDescription("Generated VCML Resources");
    compileOutput.setOutputDirectory("./VCML");
    compileOutput.setOverrideExistingResources(true);
    compileOutput.setCreateOutputDirectory(true);
    compileOutput.setCleanUpDerivedResources(true);
    compileOutput.setSetDerivedProperty(false);
    return CollectionLiterals.<OutputConfiguration>newHashSet(compileOutput);
  }
  
  public OutputConfiguration getCompileOutput() {
    Set<OutputConfiguration> _outputConfigurations = this.getOutputConfigurations();
    Iterator<OutputConfiguration> _iterator = _outputConfigurations.iterator();
    return _iterator.next();
  }
  
  public String getCompileFolderName() {
    OutputConfiguration _compileOutput = this.getCompileOutput();
    String _outputDirectory = _compileOutput.getOutputDirectory();
    return _outputDirectory.replace("./", "");
  }
}
