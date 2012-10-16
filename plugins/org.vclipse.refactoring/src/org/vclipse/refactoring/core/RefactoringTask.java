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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.xtext.validation.FeatureBasedDiagnostic;
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
		StringBuffer taskBuffer = new StringBuffer("Checking initial conditions for ").append(context.getLabel());
		SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), 10);
		EObject element = context.getSourceElement();
		EObject container = EcoreUtil.getRootContainer(element);
		if(pm.isCanceled()) {
			return RefactoringStatus.create(Status.CANCEL_STATUS);
		}
		RefactoringStatus status = validate(sm, container);
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
			StringBuffer taskBuffer = new StringBuffer("Checking final conditions for re-factoring ").append(context.getLabel());
			SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), 10);
			Object modified = modelChange.getModifiedElement();
			if(modified instanceof EObject) {
				EObject modifiedEObject = (EObject)modified;
				EObject container = EcoreUtil.getRootContainer(modifiedEObject);
				if(sm.isCanceled()) {
					return RefactoringStatus.create(Status.CANCEL_STATUS);
				}
				RefactoringStatus status = validate(sm, container);
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
		EValidator.Registry validatorRegistry = utility.getInstance(EValidator.Registry.class, object);
		EPackage epackage = object.eClass().getEPackage();
		EValidator validator = validatorRegistry.getEValidator(epackage);
		BasicDiagnostic diagnostics = new BasicDiagnostic();
		if(pm.isCanceled()) {
			return RefactoringStatus.create(Status.CANCEL_STATUS);
		}
		validator.validate(object, diagnostics, Maps.newHashMap()); 
		List<Diagnostic> foundDiagnostics = diagnostics.getChildren();
		Iterator<Diagnostic> validationIterator = Iterables.filter(foundDiagnostics, new Predicate<Diagnostic>() {
			@Override
			public boolean apply(Diagnostic diagnostic) {
				if(diagnostic instanceof FeatureBasedDiagnostic) {
					return FeatureBasedDiagnostic.ERROR == ((FeatureBasedDiagnostic)diagnostic).getSeverity();
				}
				return false;
			}
		}).iterator();
		if(validationIterator.hasNext()) {
			Diagnostic diagnostic = validationIterator.next();
			return RefactoringStatus.createFatalErrorStatus(diagnostic.getMessage());
		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}

	public void dispose() {
		modelChange = null;
		context = null;
	}
}
