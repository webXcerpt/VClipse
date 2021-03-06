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
package org.vclipse.refactoring.guice;

import org.eclipse.emf.compare.match.IEqualityHelperFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.base.ui.util.IExtendedImageHelper;
import org.vclipse.refactoring.IPreviewObjectComputer;
import org.vclipse.refactoring.IRefactoringConfiguration;
import org.vclipse.refactoring.IRefactoringUIConfiguration;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.core.DefaultContainerPreviewComputer;
import org.vclipse.refactoring.core.RefactoringConfiguration;
import org.vclipse.refactoring.ui.RefactoringUIConfiguration;
import org.vclipse.refactoring.utils.VcmlEqualityHelper;

public class RefactoringModule extends AbstractGenericModule {

	protected RefactoringPlugin plugin;
	
	public RefactoringModule(RefactoringPlugin plugin) {
		this.plugin = plugin;
	}
	
	public AbstractUIPlugin bindPlugin() {
		return plugin;
	}
	
	public IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore();
	}
	
	public Class<? extends IExtendedImageHelper> bindImageHelper() {
		return ClasspathAwareImageHelper.class;
	}
	
	public Class<? extends IPreviewObjectComputer> bindPreviewObjectComputer() {
		return DefaultContainerPreviewComputer.class;
	}
	
	public Class<? extends IRefactoringConfiguration> bindRefactoringConfiguration() {
		return RefactoringConfiguration.class;
	}
	
	public Class<? extends IRefactoringUIConfiguration> bindRefactoringUIConfiguration() {
		return RefactoringUIConfiguration.class;
	}
	
	public Class<? extends IEqualityHelperFactory> bindEqualityHelperFactory() {
		return VcmlEqualityHelper.class;
	}
}
