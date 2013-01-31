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
package org.vclipse.refactoring.core;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.ltk.core.refactoring.RefactoringTickProvider;
import org.eclipse.xtext.validation.FeatureBasedDiagnostic;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.changes.RootChange;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.refactoring.utils.Extensions;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RefactoringTask extends Refactoring {

	private RootChange modelChange;
	private IRefactoringUIContext context;
	
	@Inject 
	private RefactoringRunner runner;
	
	@Inject
	private Extensions extensions;
	
	@Inject
	private EntrySearch search;
	
	public void setContext(IRefactoringUIContext context) {
		this.context = context;
	}

	public RootChange getChange(IProgressMonitor pm) throws CoreException {
		if(modelChange == null) {
			StringBuffer taksBuffer = new StringBuffer("Creating a change description for ");
			taksBuffer.append(context.getLabel());
			SubMonitor sm = SubMonitor.convert(pm, taksBuffer.toString(), 10);
			try {
				createChange(sm);
				sm.done();
			} catch(CoreException exception) {
				sm.done();
				throw exception;
			} 
		}
		return modelChange;
	}
	
	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		RefactoringStatus refactoringStatus = RefactoringStatus.create(Status.OK_STATUS);
		SubMonitor sm = SubMonitor.convert(pm, new StringBuffer("Checking initial conditions for ").append(context.getLabel()).toString(), 10);
		EObject element = context.getSourceElement();
		EObject container = EcoreUtil.getRootContainer(element);
		refactoringStatus = sm.isCanceled() ? RefactoringStatus.create(Status.CANCEL_STATUS) : validate(sm, container);
		sm.done();
		return refactoringStatus;
	}
	
	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		SubMonitor sm = SubMonitor.convert(pm, "Creating a change description.", 20);
		try {
			modelChange = new RootChange(context);				
			modelChange.perform(pm);
			sm.done();
		} catch(CoreException exception) {
			RefactoringPlugin.log(IStatus.ERROR, exception.getMessage());
			modelChange = null;
			sm.done();
			throw exception;
		}
		return modelChange;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		RefactoringStatus refactoringStatus = RefactoringStatus.create(Status.OK_STATUS);
		if(modelChange == null) {
			createChange(pm);
		}
		RefactoringTickProvider tickProvider = getRefactoringTickProvider();
		StringBuffer taskBuffer = new StringBuffer("Checking final conditions for re-factoring ");
		taskBuffer.append(context.getLabel());
		SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), tickProvider.getCheckFinalConditionsTicks());
		Object modified = modelChange.getModifiedElement();
		if(modified instanceof EObject) {
			EObject modifiedEObject = (EObject)modified;
			EObject container = EcoreUtil.getRootContainer(modifiedEObject);
			refactoringStatus = pm.isCanceled() ? RefactoringStatus.create(Status.CANCEL_STATUS) : validate(pm, container);
		}
		sm.done();
		return refactoringStatus;
	}

	@Override
	public String getName() {
		return getClass().getName();
	}
	
	public void dispose() {
		modelChange = null;
		context = null;
	}
	
	private RefactoringStatus validate(IProgressMonitor pm, EObject object) {
		RefactoringStatus refactoringStatus = RefactoringStatus.create(Status.OK_STATUS);
		EValidator.Registry validatorRegistry = extensions.getInstance(EValidator.Registry.class, object);
		EPackage epackage = object.eClass().getEPackage();
		EValidator validator = validatorRegistry.getEValidator(epackage);
		BasicDiagnostic diagnostics = new BasicDiagnostic();
		if(pm.isCanceled()) {
			refactoringStatus = RefactoringStatus.create(Status.CANCEL_STATUS);
			pm.done();
		} else {
			SubMonitor sm = SubMonitor.convert(pm, "Validating re-factoring.", IProgressMonitor.UNKNOWN);
			validator.validate(object, diagnostics, Maps.newHashMap()); 
			Iterator<Diagnostic> validationIterator = Iterables.filter(diagnostics.getChildren(), new Predicate<Diagnostic>() {
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
				refactoringStatus = RefactoringStatus.createFatalErrorStatus(diagnostic.getMessage());
			}
			sm.done();
		}
		return refactoringStatus;
	}
}
