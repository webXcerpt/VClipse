package org.vclipse.refactoring;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class RefactoringPlugin extends AbstractUIPlugin {

	public static final String ID = "org.vclipse.refactoring"; //$NON-NLS-1$

	private static RefactoringPlugin plugin;
	
	private Injector injector;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public static RefactoringPlugin getInstance() {
		return plugin;
	}
	
	public Injector getInjector() {
		if(injector == null) {
			injector = Guice.createInjector(new RefactoringModule(this));
		}
		return injector;
	}
	
	public static void log(String message, int severity) {
		log(message, severity, null);
	}
	
	public static void log(String message, Throwable throwable) {
		log(message, IStatus.ERROR, throwable);
	}
	
	public static void log(String message, int severity, Throwable throwable) {
		getInstance().getLog().log(throwable == null ? new Status(severity, ID, message) : new Status(severity, ID, IStatus.OK, message, throwable));
	}
}
