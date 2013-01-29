/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.tests.compare

import com.google.inject.Guice
import org.eclipse.xtext.junit4.IInjectorProvider
import org.eclipse.xtext.ui.shared.internal.SharedModule
import org.vclipse.vcml.compare.injection.VcmlCompareModule
import org.vclipse.vcml.ui.VCMLUiModule
import org.vclipse.vcml.ui.internal.VCMLActivator
import org.vclipse.vcml.compare.VcmlComparePlugin

import org.eclipse.xtext.util.Modules2

/*
 * Injector provider for 
 */
class CompareInjectorProvider implements IInjectorProvider {
 
	override getInjector() {
		Guice::createInjector(
			Modules2::mixin(
				new VcmlCompareModule(VcmlComparePlugin::instance), new VCMLUiModule(VCMLActivator::instance), new SharedModule
			)
		)
	}
}