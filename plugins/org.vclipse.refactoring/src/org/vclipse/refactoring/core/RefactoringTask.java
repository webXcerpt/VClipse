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

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusContext;
import org.eclipse.xtext.diagnostics.AbstractDiagnostic;
import org.vclipse.base.ui.util.EditorUtilsExtensions;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.ui.RefactoringUtility;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class RefactoringTask extends Refactoring {

	private ModelChange modelChange;
	private IRefactoringUIContext context;
	
	@Inject
	private RefactoringUtility utility;
	
	@Inject 
	private RefactoringRunner runner;
	
	public RefactoringTask(IRefactoringUIContext context, RefactoringRunner runner, RefactoringUtility utility) {
		this.context = context;
		this.runner = runner;
		this.utility = utility;
	}
	
	public ModelChange getChange() {
		if(modelChange == null) {
			IProgressMonitor pm = EditorUtilsExtensions.getProgressMonitor();
			try {
				createChange(pm);
			} catch(CoreException exception) {
				RefactoringPlugin.log(exception.getMessage(), exception);
			} finally {
				pm.done();
			}
		}
		return modelChange;
	}
	
	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		// at the moment a re-factoring is not executed if there are any errors in the model
		EObject element = context.getSourceElement();
		Resource resource = element.eResource();
		EList<Diagnostic> errors = resource.getErrors();
		if(resource != null && errors.isEmpty()) {
			return RefactoringStatus.create(Status.OK_STATUS);			
		} else {
			Iterator<Diagnostic> diagnostics = Iterables.filter(errors, new Predicate<Diagnostic>() {
				@Override
				public boolean apply(Diagnostic diagnostic) {
					return diagnostic instanceof AbstractDiagnostic;
				}
			}).iterator();
			if(diagnostics.hasNext()) {
				final Diagnostic diagnostic = diagnostics.next();
				return RefactoringStatus.createFatalErrorStatus(diagnostic.getMessage(), new RefactoringStatusContext() {
					@Override
					public Object getCorrespondingElement() {
						return ((AbstractDiagnostic)diagnostic).getCode();
					}
				});
			}
			return RefactoringStatus.createInfoStatus("not the best conditions for a re-factoring");
		}
	}
	
	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		pm.beginTask("Creating a change description", IProgressMonitor.UNKNOWN);
		modelChange = new ModelChange(context, runner, utility);	
		modelChange.perform(pm);
		pm.done();
		return modelChange;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return checkInitialConditions(pm);
	}

	@Override
	public String getName() {
		return getClass().getName();
	}
}
