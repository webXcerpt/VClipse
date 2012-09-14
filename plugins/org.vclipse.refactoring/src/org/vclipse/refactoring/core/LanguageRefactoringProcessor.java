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
package org.vclipse.refactoring.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.ui.IUIRefactoringContext;
import org.vclipse.refactoring.ui.RefactoringUtility;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class LanguageRefactoringProcessor extends RefactoringProcessor {

	private IUIRefactoringContext context;
	
	@Inject
	private Provider<ContextBasedChange> contextBasedChange;
	
	@Inject
	private RefactoringUtility utility;
	
	@Inject
	private RefactoringRunner refactoringRunner;
	
	private ModelBasedChange modelBaseChange;
	
	public void setContext(IUIRefactoringContext context) {
		this.context = context;
	}
	
	public IUIRefactoringContext getContext() {
		return context;
	}
	 
	@Override
	public Object[] getElements() {
		if(modelBaseChange == null) {
			try {
				createChange(new NullProgressMonitor());
			} catch (Exception exception) {
				RefactoringPlugin.log(exception.getMessage(), exception);
			}
		}
		return new Object[]{modelBaseChange};
	}

	@Override
	public String getIdentifier() {
		return utility.getRefactoringText(context);
	}

	@Override
	public String getProcessorName() {
		return getClass().getSimpleName();
	}

	@Override
	public boolean isApplicable() throws CoreException {
		return true;
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		Resource resource = context.getSourceElement().eResource();
		if(resource.getErrors().isEmpty()) {
			return RefactoringStatus.create(Status.OK_STATUS);			
		} else {
			return RefactoringStatus.create(Status.CANCEL_STATUS);
		}
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context) throws CoreException, OperationCanceledException {
		Resource resource = this.context.getSourceElement().eResource();
		if(resource.getErrors().isEmpty()) {
			return RefactoringStatus.create(Status.OK_STATUS);			
		} else {
			return RefactoringStatus.create(Status.CANCEL_STATUS);
		}
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		modelBaseChange = new ModelBasedChange(this, refactoringRunner, utility);
		modelBaseChange.add(contextBasedChange.get());
		return modelBaseChange;
	}

	@Override
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants sharedParticipants) throws CoreException {
		return null;
	}
}
