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
package org.vclipse.dependency.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.vclipse.vcml.ui.resources.VcmlResourcesStateProvider;

import com.google.inject.Provider;

/**
 * Use this class to register components to be used within the IDE.
 */
public class DependencyUiModule extends org.vclipse.dependency.ui.AbstractDependencyUiModule {
	
	public DependencyUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}
	
	public Provider<IAllContainersState> provideIAllContainersState() {
		return VcmlResourcesStateProvider.getInstance();
	}
}
