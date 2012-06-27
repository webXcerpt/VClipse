
package org.vclipse.constraint;

import org.vclipse.constraint.ConstraintStandaloneSetupGenerated;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class ConstraintStandaloneSetup extends ConstraintStandaloneSetupGenerated{

	public static void doSetup() {
		new ConstraintStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

