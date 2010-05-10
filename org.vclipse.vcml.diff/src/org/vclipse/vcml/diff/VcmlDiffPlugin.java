package org.vclipse.vcml.diff;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class VcmlDiffPlugin extends AbstractUIPlugin {

	/**
	 *  The plug-in ID
	 */
	public static final String ID = "org.vclipse.vcml.diff";

	/**
	 *  The shared instance
	 */
	private static VcmlDiffPlugin plugin;
	
	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static VcmlDiffPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * @param message
	 * @param severity
	 */
	public static void log(final String message, final int severity) {
		getDefault().getLog().log(new Status(severity, ID, IStatus.OK, message, null));
	}

	/**
	 * @param message
	 * @param throwable
	 */
	public static void log(final String message, final Throwable throwable) {
		getDefault().getLog().log(new Status(IStatus.ERROR, ID, IStatus.OK, message, throwable));
	}
}
