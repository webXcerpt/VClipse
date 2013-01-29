/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator - www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.vcml.compare.injection;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.compare.diff.FeatureFilter;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.IEqualityHelperFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.validation.MarkerCreator;
import org.vclipse.base.naming.INameProvider;
import org.vclipse.vcml.VCMLRuntimeModule;
import org.vclipse.vcml.compare.VcmlComparePlugin;
import org.vclipse.vcml.naming.VcmlNameProvider;

/**
 * Dependency injection configuration for vcml compare plug-in.
 */
public class VcmlCompareModule extends VCMLRuntimeModule {

	private VcmlComparePlugin plugin;
	
	public VcmlCompareModule(VcmlComparePlugin plugin) {
		this.plugin = plugin;
	}
	
	public AbstractUIPlugin bindPlugin() {
		return plugin;
	}
	
	public IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore();
	}
	
	public Class<? extends IEqualityHelperFactory> bindEqualityHelperFactory() {
		return DefaultEqualityHelperFactory.class;
	}
	
	public IWorkspaceRoot bindWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	public Class<? extends INameProvider> bindVcmlIDComputation() {
		return VcmlNameProvider.class;
	}

	public Class<? extends MarkerCreator> bindMarkerCreator() {
		return MarkerCreator.class;
	}
	
	public Class<? extends FeatureFilter> bindFeatureFilter() {
		return FeatureFilter.class;
	}
}