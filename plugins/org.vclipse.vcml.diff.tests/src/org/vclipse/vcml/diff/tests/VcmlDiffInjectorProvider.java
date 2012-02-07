package org.vclipse.vcml.diff.tests;

import org.eclipse.xtext.junit4.GlobalRegistries;
import org.eclipse.xtext.junit4.GlobalRegistries.GlobalStateMemento;
import org.eclipse.xtext.junit4.IInjectorProvider;
import org.eclipse.xtext.junit4.IRegistryConfigurator;
import org.vclipse.vcml.VCMLStandaloneSetup;

import com.google.inject.Injector;

public class VcmlDiffInjectorProvider implements IInjectorProvider, IRegistryConfigurator {

	protected GlobalStateMemento globalStateMemento;
	protected Injector injector;

	static {
		GlobalRegistries.initializeDefaults();
	}
	
	public Injector getInjector() {
		if (injector == null) {
			this.injector = new VCMLStandaloneSetup().createInjectorAndDoEMFRegistration();
		}
		return injector;
	}
	
	public void restoreRegistry() {
		globalStateMemento.restoreGlobalState();
	}

	public void setupRegistry() {
		globalStateMemento = GlobalRegistries.makeCopyOfGlobalState();
		if(injector != null) {
			new VCMLStandaloneSetup().register(injector);
		}
	}
}
