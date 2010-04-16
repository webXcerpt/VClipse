/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
package org.vclipse.vcml;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * 
 */
public class VCMLPlugin extends Plugin {
	
	/**
	 * 
	 */
	public static final String ID = "org.vclipse.vcml";
	
	/**
	 * 
	 */
	private static VCMLPlugin instance;
	
	
	/**
	 * @return
	 */
	public static VCMLPlugin getDefault() {
		if(instance == null) {
			instance = new VCMLPlugin();
		}
		return instance;
	}
	
	/**
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}
}
