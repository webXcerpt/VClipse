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
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusContext;
import org.eclipse.xtext.diagnostics.AbstractDiagnostic;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.changes.SourceCodeChanges;
import org.vclipse.refactoring.utils.RefactoringUtility;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class RefactoringTask extends Refactoring {

	private SourceCodeChanges modelChange;
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
	
	public SourceCodeChanges getChange(IProgressMonitor pm) throws CoreException {
		if(modelChange == null) {
			StringBuffer taksBuffer = new StringBuffer("Creating a change description for ").append(context.getLabel());
			SubMonitor sm = SubMonitor.convert(pm, taksBuffer.toString(), 10);
			try {
				createChange(pm);
				sm.worked(10);
			} catch(CoreException exception) {
				sm.worked(10);
				throw exception;
			} 
		}
		return modelChange;
	}
	
	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		String taskName = new StringBuffer("Checking initial conditions for ").append(context.getLabel()).toString();
		SubMonitor sm = SubMonitor.convert(pm, taskName, 10);
		EObject element = context.getSourceElement();
		if(pm.isCanceled()) {
			return RefactoringStatus.create(Status.CANCEL_STATUS);
		}
		RefactoringStatus status = validate(sm, element);
		sm.worked(10);
		return status;
	}
	
	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		SubMonitor sm = SubMonitor.convert(pm, "Creating a change description.", 20);
		try {
			if(sm.isCanceled()) {
				return modelChange;
			}
			modelChange = new SourceCodeChanges(context, runner, utility);
			sm.worked(10);
			if(sm.isCanceled()) {
				return modelChange;
			}
			modelChange.perform(pm);
			sm.worked(10);
		} catch(CoreException exception) {
			RefactoringPlugin.log(exception.getMessage(), exception);
			modelChange = null;
			sm.worked(20);
			throw exception;
		}
		return modelChange;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		if(modelChange == null) {
			return RefactoringStatus.createFatalErrorStatus("Could not execute validation. Refactored model is null.");
		} else {
			StringBuffer taskBuffer = new StringBuffer("Checking final conditions for re-factoring").append(context.getLabel());
			SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), 10);
			Object modified = modelChange.getModifiedElement();
			if(modified instanceof EObject) {
				EObject modifiedEObject = (EObject)modified;
				if(sm.isCanceled()) {
					return RefactoringStatus.create(Status.CANCEL_STATUS);
				}
				RefactoringStatus status = validate(sm, modifiedEObject);
				sm.worked(10);
				return status;				
			}
		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}

	@Override
	public String getName() {
		return getClass().getName();
	}
	
	private RefactoringStatus validate(IProgressMonitor pm, EObject object) {
		Resource resource = object.eResource();
		if(resource == null) {
			RefactoringStatus.createFatalErrorStatus("Could not execute validation. The resource is null.");
		} else {
			EList<Diagnostic> errors = resource.getErrors();
			if(errors.isEmpty()) {
				return RefactoringStatus.create(Status.OK_STATUS);			
			} else {
				Iterator<Diagnostic> diagnosticsIterator = Iterables.filter(errors, new Predicate<Diagnostic>() {
					@Override
					public boolean apply(Diagnostic diagnostic) {
						return diagnostic instanceof AbstractDiagnostic;
					}
				}).iterator();
				
				if(!diagnosticsIterator.hasNext()) {
					EValidator.Registry validatorRegistry = utility.getInstance(EValidator.Registry.class, object);
					EPackage epackage = object.eClass().getEPackage();
					EValidator validator = validatorRegistry.getEValidator(epackage);
					BasicDiagnostic diagnostics = new BasicDiagnostic();
					if(pm.isCanceled()) {
						return RefactoringStatus.create(Status.CANCEL_STATUS);
					}
					validator.validate(object, diagnostics, Maps.newHashMap());
					diagnosticsIterator = Iterables.filter(errors, new Predicate<Diagnostic>() {
						@Override
						public boolean apply(Diagnostic diagnostic) {
							return diagnostic instanceof AbstractDiagnostic;
						}
					}).iterator();
					if(diagnosticsIterator.hasNext()) {
						Diagnostic diagnostic = diagnosticsIterator.next();
						return RefactoringStatus.createFatalErrorStatus(diagnostic.getMessage());
					}
				} else {
					final Diagnostic diagnostic = diagnosticsIterator.next();
					return RefactoringStatus.createFatalErrorStatus(diagnostic.getMessage(), new RefactoringStatusContext() {
						@Override
						public Object getCorrespondingElement() {
							AbstractDiagnostic adiagnostic = (AbstractDiagnostic)diagnostic;
							return adiagnostic.getCode();
						}
					});
				}
			}
		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}
}
