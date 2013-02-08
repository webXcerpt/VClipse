/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator and others
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.tests.vcmlt;

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
