package org.vclipse.sap.deployment;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.osgi.framework.BundleContext;
import org.vclipse.sap.deployment.injection.DeploymentModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class DeploymentPlugin extends AbstractUIPlugin {

	public static final String ID = "org.vclipse.deployment";
	
	private static DeploymentPlugin plugin;
	
	private Injector injector;

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static DeploymentPlugin getDefault() {
		return plugin;
	}
	
	public static void log(String message, int severity, Throwable throwable) {
		getDefault().getLog().log(new Status(severity, ID, IStatus.OK, message, throwable));
	}
	
	public static void log(String message, int severity) {
		getDefault().getLog().log(new Status(severity, ID, message));
	}
	
	public Injector getInjector(AbstractGenericModule optionalModule) {
		if(injector == null) {
			if(optionalModule != null) {
				injector = Guice.createInjector(optionalModule);
			} else {
				injector = Guice.createInjector(new DeploymentModule(this));
			}
		}
		return injector;
	}
}
