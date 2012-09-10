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
package org.vclipse.refactoring.ui;

import java.util.List;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.vclipse.refactoring.core.IRefactoringContext;
import org.vclipse.refactoring.core.RefactoringCustomisation;

import com.google.common.collect.Lists;

@SuppressWarnings("unchecked")
public class RefactoringUICustomisation extends RefactoringCustomisation {

	public static final String PAGES_PREFIX = "pages_";
	public static final String SWITCH_WIDGETS_PREFIX = "switch_widgets_";
	
	public RefactoringUICustomisation() {
		collect(1, IUIRefactoringContext.class);
		collect(2);
	}
	
	public void switchWidgets(IRefactoringContext context) {
		invoke(context, SWITCH_WIDGETS_PREFIX);
	}
	
	public List<? extends UserInputWizardPage> getPages(IRefactoringContext context) {
		Object result = invoke(context, PAGES_PREFIX);
		return result instanceof List ? (List<? extends UserInputWizardPage>)result : Lists.<UserInputWizardPage>newArrayList();
	}
}
