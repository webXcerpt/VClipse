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

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.EValidator.Registry;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.serializer.ISerializer;
import org.vclipse.refactoring.ui.RefactoringUtility;

import com.google.common.collect.Maps;

public class SourceCodeChange extends NoChange {

	protected EObject originalObject;
	protected EObject refactoredObject;
	
	protected EStructuralFeature feature;
	protected Object newValue;
	
	protected EValidator validator;
	protected IQualifiedNameProvider nameProvider;
	protected ISerializer serializer;
	
	private RefactoringUtility utility;
	
	public SourceCodeChange(RefactoringUtility utility, EObject original, EObject refactoring, EStructuralFeature feature, Object newValue) {
		this.originalObject = original;
		this.refactoredObject = refactoring;
		this.feature = feature;
		this.newValue = newValue;
		this.utility = utility;
	}
	
	@Override
	public void initializeValidationData(IProgressMonitor pm) {
		Registry registry = utility.getInstance(originalObject, EValidator.Registry.class);
		validator = registry.getEValidator(originalObject.eClass().getEPackage());
		nameProvider = utility.getInstance(originalObject, IQualifiedNameProvider.class);
		serializer = utility.getInstance(originalObject, ISerializer.class);
	}

	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		pm.beginTask("Validating change for " + getMonitorString(), IProgressMonitor.UNKNOWN);
		BasicDiagnostic diagnostics = new BasicDiagnostic();
		HashMap<Object, Object> validationContext = Maps.newHashMap();
		validator.validate(originalObject, diagnostics, validationContext);
		validator.validate(refactoredObject, diagnostics, validationContext);
		pm.done();
		return RefactoringStatus.create(Status.OK_STATUS);
	}

	@SuppressWarnings("unchecked")
	public RefactoringStatus refactor(IProgressMonitor pm) throws CoreException {
		if(isEnabled()) {
			pm.beginTask("Executing re-factoring for " + getMonitorString(), IProgressMonitor.UNKNOWN);
			EReference containment = originalObject.eContainmentFeature();
			if(containment == feature) {
				originalObject.eContainer().eSet(containment, newValue);
			} else {
				Object entry = originalObject.eGet(feature);
				if(entry instanceof EList<?>) {
					((EList<EObject>)entry).add((EObject)newValue);
				} else {
					originalObject.eSet(feature, newValue);				
				}
			}
			pm.done();
		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}

	@Override
	public Object getModifiedElement() {
		return originalObject;
	}
	
	@Override
	public String getName() {
		return "Re-factoring for " + getMonitorString();
	}
	
	private String getMonitorString() {
		QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(originalObject);
		return qualifiedName == null ? originalObject.eClass().getName() : qualifiedName.getLastSegment();
	}
}
