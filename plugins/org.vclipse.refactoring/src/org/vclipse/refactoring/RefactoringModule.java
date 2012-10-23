/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.refactoring;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.base.ui.util.IExtendedImageHelper;
import org.vclipse.refactoring.core.DefaultPreviewObjectComputer;

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
		return DefaultPreviewObjectComputer.class;
	}
}
