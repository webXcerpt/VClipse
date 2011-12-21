
package org.vclipse.configscan.vcmlt;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class VcmlTStandaloneSetup extends VcmlTStandaloneSetupGenerated{

	public static void doSetup() {
		new VcmlTStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

