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
package org.vclipse.refactoring.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.changes.RootChange;
import org.vclipse.refactoring.core.RefactoringTask;

import com.google.common.collect.Lists;

public class RefactoringWizard extends org.eclipse.ltk.ui.refactoring.RefactoringWizard {

	private List<? extends UserInputWizardPage> pages;

	public RefactoringWizard(List<? extends UserInputWizardPage> pages, Refactoring refactoring, int flags) {
		super(refactoring, flags);
		setDefaultPageTitle("Language Refactoring");
		setWindowTitle("Language Refactoring");
		this.pages = pages == null ? Lists.<UserInputWizardPage>newArrayList() : pages;
		
		setNeedsProgressMonitor(true);
		setForcePreviousAndNextButtons(true);
	}
	
	@Override
	public boolean performFinish() {
		IWizardContainer container = getContainer();
		Refactoring refactoring = getRefactoring();
		if(refactoring instanceof RefactoringTask) {
			final RefactoringTask modelRefactoring = (RefactoringTask)refactoring;
			try {
				container.run(false, true, new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
						try {
							RootChange rootChange = modelRefactoring.getChange(pm);
							for(Change change : rootChange.getChildren()) {
								change.perform(pm);
								change.dispose();
							}
							rootChange.dispose();
							pm.done();
						} catch(CoreException exception) {
							throw new InvocationTargetException(exception);
						}
					}
				});
			} catch(Exception exception) {
				Throwable cause = exception.getCause();
				RefactoringPlugin.log(IStatus.ERROR, cause == null ? exception.getMessage() : cause.getMessage());
			}
		}
		return true && super.performFinish();
	}
	
	@Override
	public boolean performCancel() {
		boolean cancel = super.performCancel();
		Refactoring refactoring = getRefactoring();
		if(refactoring instanceof RefactoringTask) {
			final RefactoringTask modelRefactoring = (RefactoringTask)refactoring;
			modelRefactoring.dispose();
		}
		return cancel;
	}

	@Override
	protected void addUserInputPages() {
		for(UserInputWizardPage page : pages) {
			addPage(page);
		}
	}
}
