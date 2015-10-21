/** 
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.idoc2jcoidoc.injection

import org.eclipse.jface.preference.IPreferenceStore
import org.eclipse.ui.plugin.AbstractUIPlugin
import org.eclipse.xtext.service.AbstractGenericModule
import org.vclipse.connection.IConnectionHandler
import org.vclipse.connection.VClipseConnectionPlugin
import org.vclipse.idoc2jcoidoc.DefaultIDoc2JCoIDocProcessor
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor
// import org.vclipse.vcml.ui.VCMLUiPlugin
// import org.vclipse.vcml2idoc.VCML2IDocPlugin
// import org.vclipse.vcml2idoc.transformation.VCML2IDocSwitch
import com.google.inject.Binder
import com.google.inject.Injector
import com.google.inject.name.Names

class IDoc2JCoIDocModule extends AbstractGenericModule {
	AbstractUIPlugin plugin

	new(AbstractUIPlugin activator) {
		plugin = activator
	}

	def IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore()
	}

	override void configure(Binder binder) {
		super.configure(binder)
		// var Injector vcmlInjector = VCMLUiPlugin.getInjector()
		// var Injector vcml2IDocInjector = VCML2IDocPlugin.getDefault().getInjector()
		var Injector connectionInjector = VClipseConnectionPlugin.getDefault().getInjector()
		// binder.bind(IPreferenceStore).annotatedWith(Names.named(VCMLUiPlugin.ID)).toInstance(
		// 	vcmlInjector.getInstance(IPreferenceStore))
		// binder.bind(IPreferenceStore).annotatedWith(Names.named(VCML2IDocPlugin.ID)).toInstance(
		//	vcml2IDocInjector.getInstance(IPreferenceStore))
		binder.bind(IPreferenceStore).annotatedWith(Names.named(IDoc2JCoIDocPlugin.ID)).toInstance(
			plugin.getPreferenceStore())
		// binder.bind(VCML2IDocSwitch).toInstance(vcml2IDocInjector.getInstance(VCML2IDocSwitch))
		binder.bind(IConnectionHandler).toInstance(connectionInjector.getInstance(IConnectionHandler))
	}

	def Class<? extends IIDoc2JCoIDocProcessor> bindIDoc2JCoIDocProcessor() {
		return DefaultIDoc2JCoIDocProcessor
	}

}
