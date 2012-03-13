package org.vclipse.sap.deployment;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.osgi.framework.BundleContext;
import org.vclipse.base.ui.dialogs.ErrorDialog;
import org.vclipse.sap.deployment.injection.DeploymentModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class DeploymentPlugin extends AbstractUIPlugin {

	public static final String ID = "org.vclipse.sap.deployment";
	
	protected static DeploymentPlugin plugin;
	
	protected Injector injector;
	
	protected DeploymentModule injectionModule;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		injectionModule = new DeploymentModule(this);
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public static DeploymentPlugin getDefault() {
		return plugin;
	}
	
	public static Image getImage(String key) {
		return getDefault().getImageRegistry().get(key);
	}
	
	public Injector getInjector(AbstractGenericModule optionalModule) {
		if(injector == null) {
			if(optionalModule != null) {
				injector = Guice.createInjector(optionalModule);
			} else if(injectionModule != null) {
				injector = Guice.createInjector(injectionModule);
			} else {
				throw new IllegalArgumentException("Injection module not initialized.");
			}
		}
		return injector;
	}
	
	public Injector getInjector() {
		return getInjector(injectionModule);
	}
	
	public static void showErrorDialog(String dialogTitle, String message, IStatus status) {
		showErrorDialog(null, dialogTitle, message, status);
	}
	
	public static void showErrorDialog(Exception exception, String dialogTitle, String message) {
		showErrorDialog(exception, dialogTitle, message, null);
	}
	
	public static void showErrorDialog(final Exception exception, final String dialogTitle, final String message, final IStatus status) {
		log(message, null);
		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				new ErrorDialog(display.getActiveShell(), dialogTitle, message, status).open();
			}
		});
	}
	
	public static void log(String message, int severity) {
		log(message, severity, null);
	}
	
	public static void log(String message, Throwable throwable) {
		log(message, IStatus.ERROR, throwable);
	}
	
	public static void log(String message, int severity, Throwable throwable) {
		getDefault().getLog().log(throwable == null ? 
				new Status(severity, ID, message) : 
					new Status(severity, ID, IStatus.OK, message, throwable));
	}
}
