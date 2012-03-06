package org.vclipse.vcml.mm;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class VcmlMetaModelPlugin implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		VcmlMetaModelPlugin.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		VcmlMetaModelPlugin.context = null;
	}

}
