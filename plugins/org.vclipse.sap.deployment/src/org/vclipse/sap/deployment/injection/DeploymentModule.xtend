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
package org.vclipse.sap.deployment.injection

import org.eclipse.core.resources.IWorkspaceRoot
import org.eclipse.emf.compare.match.IEqualityHelperFactory
import org.eclipse.jface.preference.IPreferenceStore
import org.eclipse.ui.plugin.AbstractUIPlugin
import org.vclipse.base.ui.util.ClasspathAwareImageHelper
import org.vclipse.base.ui.util.IExtendedImageHelper
import org.vclipse.connection.IConnectionHandler
import org.vclipse.connection.VClipseConnectionPlugin
import org.vclipse.idoc2jcoidoc.DefaultIDoc2JCoIDocProcessor
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor
import org.vclipse.sap.deployment.DeploymentPlugin
import org.vclipse.sap.deployment.OneClickWorkflow
import org.vclipse.vcml.VCMLRuntimeModule
import org.vclipse.vcml.compare.VCMLCompareOperation
import org.vclipse.vcml.compare.VCMLComparePlugin
import org.vclipse.vcml.ui.VCMLUiPlugin
import org.vclipse.vcml2idoc.VCML2IDocPlugin
import org.vclipse.vcml2idoc.transformation.VCML2IDocSwitch
import com.google.inject.Binder
import com.google.inject.Injector
import com.google.inject.name.Names

class DeploymentModule extends VCMLRuntimeModule {
	DeploymentPlugin plugin

	new(DeploymentPlugin plugin) {
		this.plugin = plugin
	}

	def IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore()
	}

	def AbstractUIPlugin bindPlugin() {
		return plugin
	}

	override void configure(Binder binder) {
		super.configure(binder)
		var Injector connectionInjector = VClipseConnectionPlugin.getDefault().getInjector()
		binder.bind(IConnectionHandler).toInstance(connectionInjector.getInstance(IConnectionHandler))
		var Injector vcmlInjector = VCMLUiPlugin.getInjector()
		binder.bind(IPreferenceStore).annotatedWith(Names.named(VCMLUiPlugin.ID)).toInstance(
			vcmlInjector.getInstance(IPreferenceStore))
		var Injector vcml2IDocInjector = VCML2IDocPlugin.getDefault().getInjector()
		binder.bind(IPreferenceStore).annotatedWith(Names.named(VCML2IDocPlugin.ID)).toInstance(
			vcml2IDocInjector.getInstance(IPreferenceStore))
		binder.bind(VCML2IDocSwitch).toInstance(vcml2IDocInjector.getInstance(VCML2IDocSwitch))
		var Injector idoc2JCoIDocInjector = IDoc2JCoIDocPlugin.getInstance().getInjector()
		binder.bind(IPreferenceStore).annotatedWith(Names.named(IDoc2JCoIDocPlugin.ID)).toInstance(
			idoc2JCoIDocInjector.getInstance(IPreferenceStore))
		var Injector vcmlCompareInjector = VCMLComparePlugin.getInstance().getInjector()
		binder.bind(IWorkspaceRoot).toInstance(vcmlCompareInjector.getInstance(IWorkspaceRoot))
		binder.bind(IEqualityHelperFactory).toInstance(vcmlCompareInjector.getInstance(IEqualityHelperFactory))
	}

	def Class<? extends IExtendedImageHelper> bindImageHelper() {
		return ClasspathAwareImageHelper
	}

	def Class<? extends IIDoc2JCoIDocProcessor> bindIDoc2JCoIDocProcessor() {
		return DefaultIDoc2JCoIDocProcessor
	}

	def Class<? extends OneClickWorkflow> bindOneClickWorkflow() {
		return OneClickWorkflow
	}

	def Class<? extends VCMLCompareOperation> bindComparison() {
		return VCMLCompareOperation
	}

}
