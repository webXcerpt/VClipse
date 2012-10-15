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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
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
		IWizardContainer container = getContainer();
		Refactoring refactoring = getRefactoring();
		if(refactoring instanceof RefactoringTask) {
			final RefactoringTask modelRefactoring = (RefactoringTask)refactoring;
			try {
				container.run(false, true, new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
						try {
							SourceCodeChanges changes = modelRefactoring.getChange(pm);
							Change[] children = changes.getChildren();
							StringBuffer labelBuffer = new StringBuffer("Executing re-factoring for ").append(changes.getContext().getLabel());
							pm.beginTask(labelBuffer.toString(), children.length);
							changes.refactor(pm);
							changes.dispose();
						} catch(CoreException exception) {
							throw new InvocationTargetException(exception);
						}
					}
				});
			} catch(InvocationTargetException exception) {
				Throwable cause = exception.getCause();
				RefactoringPlugin.log(cause.getMessage(), cause);
			} catch(InterruptedException exception) {
				Throwable cause = exception.getCause();
				RefactoringPlugin.log(cause.getMessage(), cause);
			}
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
