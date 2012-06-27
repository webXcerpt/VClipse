
package org.vclipse.procedure;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class ProcedureStandaloneSetup extends ProcedureStandaloneSetupGenerated{

	public static void doSetup() {
		new ProcedureStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

