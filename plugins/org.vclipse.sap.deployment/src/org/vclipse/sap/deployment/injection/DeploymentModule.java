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
package org.vclipse.sap.deployment.injection;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.emf.compare.match.IEqualityHelperFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.base.ui.util.IExtendedImageHelper;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.idoc2jcoidoc.DefaultIDoc2JCoIDocProcessor;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor;
import org.vclipse.sap.deployment.DeploymentPlugin;
import org.vclipse.sap.deployment.OneClickWorkflow;
import org.vclipse.vcml.VCMLRuntimeModule;
import org.vclipse.vcml.compare.VCMLCompareOperation;
import org.vclipse.vcml.compare.VCMLComparePlugin;
import org.vclipse.vcml.ui.VCMLUiPlugin;
import org.vclipse.vcml2idoc.VCML2IDocPlugin;
import org.vclipse.vcml2idoc.transformation.VCML2IDocSwitch;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class DeploymentModule extends VCMLRuntimeModule {

	private DeploymentPlugin plugin;
	
	public DeploymentModule(DeploymentPlugin plugin) {
		this.plugin = plugin;
	}
	
	public IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore();
	}
	
	public AbstractUIPlugin bindPlugin() {
		return plugin;
	}
	
	@Override
	public void configure(Binder binder) {
		super.configure(binder);
		
		Injector connectionInjector = VClipseConnectionPlugin.getDefault().getInjector();
		binder.bind(IConnectionHandler.class).toInstance(connectionInjector.getInstance(IConnectionHandler.class));
		
		Injector vcmlInjector = VCMLUiPlugin.getInjector();
		binder.bind(IPreferenceStore.class).annotatedWith(Names.named(VCMLUiPlugin.ID)).
			toInstance(vcmlInjector.getInstance(IPreferenceStore.class));
		
		Injector vcml2IDocInjector = VCML2IDocPlugin.getDefault().getInjector();
		binder.bind(IPreferenceStore.class).annotatedWith(Names.named(VCML2IDocPlugin.ID)).
			toInstance(vcml2IDocInjector.getInstance(IPreferenceStore.class));
		binder.bind(VCML2IDocSwitch.class).toInstance(vcml2IDocInjector.getInstance(VCML2IDocSwitch.class));
		
		Injector idoc2JCoIDocInjector = IDoc2JCoIDocPlugin.getInstance().getInjector();
		binder.bind(IPreferenceStore.class).annotatedWith(Names.named(IDoc2JCoIDocPlugin.ID)).
			toInstance(idoc2JCoIDocInjector.getInstance(IPreferenceStore.class));

		Injector vcmlCompareInjector = VCMLComparePlugin.getInstance().getInjector();
		binder.bind(IWorkspaceRoot.class).toInstance(vcmlCompareInjector.getInstance(IWorkspaceRoot.class));
		binder.bind(IEqualityHelperFactory.class).toInstance(vcmlCompareInjector.getInstance(IEqualityHelperFactory.class));
	}
	
	public Class<? extends IExtendedImageHelper> bindImageHelper() {
		return ClasspathAwareImageHelper.class;
	}

	public Class<? extends IIDoc2JCoIDocProcessor> bindIDoc2JCoIDocProcessor() {
		return DefaultIDoc2JCoIDocProcessor.class;
	}

	public Class<? extends OneClickWorkflow> bindOneClickWorkflow() {
		return OneClickWorkflow.class;
	}
	
	public Class<? extends VCMLCompareOperation> bindComparison() {
		return VCMLCompareOperation.class;
	}
}
