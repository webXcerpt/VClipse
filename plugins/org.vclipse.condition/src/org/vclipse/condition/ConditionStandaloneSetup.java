
package org.vclipse.condition;

import org.vclipse.condition.ConditionStandaloneSetupGenerated;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class ConditionStandaloneSetup extends ConditionStandaloneSetupGenerated{

	public static void doSetup() {
		new ConditionStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

