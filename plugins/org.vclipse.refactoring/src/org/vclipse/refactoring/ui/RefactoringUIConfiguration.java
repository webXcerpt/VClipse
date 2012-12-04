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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.IRefactoringUIConfiguration;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.core.RefactoringConfiguration;

import com.google.common.collect.Lists;

@SuppressWarnings("unchecked")
public class RefactoringUIConfiguration extends RefactoringConfiguration implements IRefactoringUIConfiguration {

	public static final String PAGES_PREFIX = "pages_";
	public static final String SWITCH_WIDGETS_PREFIX = "switch_widgets_";
	
	public RefactoringUIConfiguration() {
		collect(1, IRefactoringUIContext.class);
		collect(2);
	}
	
	@Override
	public void configureWidgets(IRefactoringContext context) {
		invoke(context, SWITCH_WIDGETS_PREFIX);
	}

	@Override
	public List<? extends UserInputWizardPage> provideWizardPages(IRefactoringContext context) {
		Object result = invoke(context, PAGES_PREFIX);
		return result instanceof List ? (List<? extends UserInputWizardPage>)result : Lists.<UserInputWizardPage>newArrayList();
	}
	 
	protected void addModifyListener(Text text, ModifyListenerDelegate newListener) {
		Listener[] listeners = text.getListeners(SWT.Modify);
		for(Listener listener : listeners) {
			text.removeListener(SWT.Modify, listener);			
		}
		newListener.handleAndDelegateTo(listeners);
		text.addModifyListener(newListener);
	}
}
