/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.tests.compare

import com.google.inject.Guice
import org.eclipse.xtext.junit4.IInjectorProvider
import org.eclipse.xtext.ui.shared.internal.SharedModule
import org.vclipse.vcml.compare.injection.VCMLCompareModule
import org.vclipse.vcml.ui.VCMLUiModule
import org.vclipse.vcml.ui.internal.VCMLActivator
import org.vclipse.vcml.compare.VCMLComparePlugin
import org.eclipse.xtext.util.Modules2

/**
 * Injector provider for VCML Compare tests.
 */
class VCMLCompareInjectorProvider implements IInjectorProvider {
 
	override getInjector() {
		Guice::createInjector(
			Modules2::mixin(
				new VCMLCompareModule(VCMLComparePlugin::getInstance), 
				new VCMLUiModule(VCMLActivator::instance), 
				new SharedModule
			)
		)
	}
}