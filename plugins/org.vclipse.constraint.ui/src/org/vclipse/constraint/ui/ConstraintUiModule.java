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
package org.vclipse.constraint.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.vclipse.constraint.ui.refactoring.ConstraintRefactoring;
import org.vclipse.refactoring.IPreviewObjectComputer;
import org.vclipse.refactoring.IRefactoringConfiguration;
import org.vclipse.refactoring.IRefactoringExecuter;
import org.vclipse.refactoring.IRefactoringUIConfiguration;
import org.vclipse.vcml.refactoring.VCMLConfiguration;
import org.vclipse.vcml.ui.refactoring.VCMLUICustomisation;
import org.vclipse.vcml.ui.resources.VcmlResourcesStateProvider;

import com.google.inject.Provider;

/**
 * Use this class to register components to be used within the IDE.
 */
public class ConstraintUiModule extends org.vclipse.constraint.ui.AbstractConstraintUiModule {
	
	public ConstraintUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}
	
	public Provider<IAllContainersState> provideIAllContainersState() {
		return VcmlResourcesStateProvider.getInstance();
	}
	
	/**
	 * Vcml Re-factoring bindings
	 */
	public Class<? extends IPreviewObjectComputer> bindRelevantEntityComputer() {
		return org.vclipse.vcml.ui.refactoring.PreviewEntityComputer.class;
	}
	
	public Class<? extends IRefactoringConfiguration> bindRefactoringConfiguration() {
		return VCMLConfiguration.class;
	}
	
	public Class<? extends IRefactoringUIConfiguration> bindRefactoringUIConfiguration() {
		return VCMLUICustomisation.class;
	}
	
	public Class<? extends IRefactoringExecuter> bindRefactoringExecuter() {
		return ConstraintRefactoring.class;
	}
}
