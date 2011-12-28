package org.vclipse.configscan;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.vclipse.configscan.Module;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.vclipse.configscan"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private Injector injector;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public Injector getInjector() {
		if(injector == null) {
			injector = Guice.createInjector(new Module());
		}
		return injector;
	}
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	
	@Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        super.initializeImageRegistry(registry);
        Bundle bundle = Platform.getBundle(PLUGIN_ID);

        
        ImageDescriptor success = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/s.gif"), null));
        ImageDescriptor failure = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/e.gif"), null));
        ImageDescriptor relaunch = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/relaunch.gif"), null));
        ImageDescriptor relaunchf = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/relaunchf.gif"), null));
        ImageDescriptor select_next = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/select_next.gif"), null));
        ImageDescriptor select_prev = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/select_prev.gif"), null));
        ImageDescriptor stop = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/stop.gif"), null));
        ImageDescriptor w = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/w.gif"), null));
        ImageDescriptor failures = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/failures.gif"), null));
        ImageDescriptor flatLayout = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/flatLayout.gif"), null));
        ImageDescriptor hierarchicalLayout = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/hierarchicalLayout.gif"), null));
        ImageDescriptor expandAll = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/expandall.gif"), null));
        ImageDescriptor collapseAll = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/collapseall.gif"), null));
        ImageDescriptor configscan = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/Fysbee.png"), null));
        ImageDescriptor cmlt = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/tests.png"), null));
        
        registry.put("s", success);
        registry.put("f", failure);
        registry.put("relaunch", relaunch);
        registry.put("relaunchf", relaunchf);
        registry.put("select_next", select_next);
        registry.put("select_prev", select_prev);
        registry.put("stop", stop);
        registry.put("w", w);
        registry.put("failures", failures);
        registry.put("flatLayout", flatLayout);
        registry.put("hierarchicalLayout", hierarchicalLayout);
        registry.put("expandAll", expandAll);
        registry.put("collapseAll", collapseAll);
        registry.put("configscan", configscan);
        registry.put("cmlt", cmlt);
    }

}
