package org.vclipse.configscan.vcmlt;

import org.vclipse.vcml.VCMLStandaloneSetup;

import com.google.inject.Injector;

public class VcmlTInjectorProviderWithVCML extends VcmlTInjectorProvider {

	protected Injector injectorVCML;

	public Injector getInjector() {
		if (injectorVCML == null) {
			this.injectorVCML = new VCMLStandaloneSetup().createInjectorAndDoEMFRegistration();
		}
		return super.getInjector();
	}
	
	public void setupRegistry() {
		super.setupRegistry();
		if (injectorVCML != null)
			new VCMLStandaloneSetup().register(injectorVCML);
	}

}
