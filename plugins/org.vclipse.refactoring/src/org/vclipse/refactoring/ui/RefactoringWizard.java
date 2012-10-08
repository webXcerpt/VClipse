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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.vclipse.base.ui.util.EditorUtilsExtensions;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.changes.SourceCodeChanges;
import org.vclipse.refactoring.core.RefactoringTask;

import com.google.common.collect.Lists;

public class RefactoringWizard extends org.eclipse.ltk.ui.refactoring.RefactoringWizard {

	private List<? extends UserInputWizardPage> pages;

	public RefactoringWizard(List<? extends UserInputWizardPage> pages, Refactoring refactoring, int flags) {
		super(refactoring, flags);
		setDefaultPageTitle("Language Refactoring");
		setWindowTitle("Language Refactoring");
		this.pages = pages == null ? Lists.<UserInputWizardPage>newArrayList() : pages;
	}
	
	@Override
	public boolean performFinish() {
		Refactoring refactoring = getRefactoring();
		if(refactoring instanceof RefactoringTask) {
			RefactoringTask modelRefactoring = (RefactoringTask)refactoring;
			IProgressMonitor pm = EditorUtilsExtensions.getProgressMonitor();
			SourceCodeChanges modelChange = modelRefactoring.getChange();
			try {
				modelChange.refactor(pm);
			} catch(CoreException exception) {
				RefactoringPlugin.log(exception.getMessage(), exception);
			}
			modelChange.dispose();
		}
		return super.performFinish();
	}

	@Override
	protected void addUserInputPages() {
		for(UserInputWizardPage page : pages) {
			addPage(page);
		}
	}
}
