
package org.vclipse.vcml;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class VCMLStandaloneSetup extends VCMLStandaloneSetupGenerated{

	public static void doSetup() {
		new VCMLStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

