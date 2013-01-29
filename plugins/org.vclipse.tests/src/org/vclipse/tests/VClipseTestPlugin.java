package org.vclipse.tests;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.shared.SharedStateModule;
import org.eclipse.xtext.util.Modules2;
import org.osgi.framework.BundleContext;
import org.vclipse.vcml.VCMLRuntimeModule;
import org.vclipse.vcml.ui.VCMLUiModule;
import org.vclipse.vcml.ui.internal.VCMLActivator;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * The activator class controls the plug-in life cycle
 */
public class VClipseTestPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String ID = "org.vclipse.tests"; //$NON-NLS-1$

	// The shared instance
	private static VClipseTestPlugin plugin;
	
	private Injector injector;
	
	/**
	 * The constructor
	 */
	public VClipseTestPlugin() {
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * @return shared instance
	 */
	public static VClipseTestPlugin getInstance() {
		return plugin;
	}

	/**
	 * @return configured injector for this plugin
	 */
	public Injector getInjector() {
		if(injector == null) {
			VCMLRuntimeModule vcmlRuntime = new VCMLRuntimeModule();
			VCMLUiModule vcmlUiModule = new VCMLUiModule(VCMLActivator.getInstance());
			SharedStateModule sharedModule = new SharedStateModule();
			VClipseTestModule testModule = new VClipseTestModule(this);
			injector = Guice.createInjector(
					Modules2.mixin(testModule, vcmlRuntime, sharedModule, vcmlUiModule)
			);
		}
		return injector;
	}
}
