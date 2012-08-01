package org.vclipse.vcml.ui.resources;

import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.vclipse.vcml.ui.internal.VCMLActivator;

import com.google.inject.Injector;
import com.google.inject.Provider;

public class VcmlResourcesStateProvider implements Provider<IAllContainersState> {

	private static VcmlResourcesStateProvider provider;
	
	private VcmlResourcesState state;
	
	public static VcmlResourcesStateProvider getInstance() {
		if(provider == null) {
			provider = new VcmlResourcesStateProvider();
		}
		return provider;
	}

	@Override
	protected void finalize() throws Throwable {
		state.unregisterAsListener();
		super.finalize();
	}
	
	public IAllContainersState get() {
		if(state == null) {
			VCMLActivator activator = VCMLActivator.getInstance();
			Injector injector = activator.getInjector(VCMLActivator.ORG_VCLIPSE_VCML_VCML);
			state = injector.getInstance(VcmlResourcesState.class);
		}
		return state;
	}
}
