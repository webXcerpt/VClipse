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
package org.vclipse.refactoring.changes;

import java.util.List;

import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.EValidator.Registry;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusEntry;
import org.eclipse.xtext.diagnostics.AbstractDiagnostic;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.serializer.ISerializer;
import org.vclipse.base.ui.compare.EObjectTypedElement;
import org.vclipse.refactoring.core.DiffNode;
import org.vclipse.refactoring.utils.RefactoringUtility;

import com.google.common.collect.Maps;

public class SourceCodeChange extends NoChange {

	private RefactoringUtility utility;
	
	private EObject originalContainer;
	private EObject original;
	private EObject refactored;
	private EStructuralFeature feature;
		
	private DiffNode diffNode;
	
	private EValidator validator;
	private IQualifiedNameProvider nameProvider;
	private ISerializer serializer;
	
	public SourceCodeChange(RefactoringUtility utility) {
		this.utility = utility;
	}
	
	public void deleteChange(EObject container, EObject existing, EStructuralFeature feature) {
		this.originalContainer = container;
		this.original = existing;
		this.feature = feature;
		init();
	}
	
	public void addChange(EObject container, EObject refactored, EStructuralFeature feature) {
		this.originalContainer = container;
		this.refactored = refactored;
		this.feature = feature;
		init();
	}
	
	public void entryChange(EObject original, EObject refactored, EStructuralFeature feature) {
		this.original = original;
		this.refactored = refactored;
		this.feature = feature;
		init();
	}
	
	protected void init() {
		EObject initObject = original == null ? originalContainer : original;
		Registry registry = utility.getInstance(EValidator.Registry.class, initObject);
		EPackage epackage = initObject.eClass().getEPackage();
		validator = registry.getEValidator(epackage);
		nameProvider = utility.getInstance(IQualifiedNameProvider.class, initObject);
		serializer = utility.getInstance(ISerializer.class, initObject);
	}
	
	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		EObject handleWithObject = refactored == null ? original == null ? originalContainer : original : refactored;
		StringBuffer taskBuffer = new StringBuffer("Validating change for ").append(getName(utility, handleWithObject));
		SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), 10);
		BasicDiagnostic diagnostics = new BasicDiagnostic();
		validator.validate(handleWithObject, diagnostics, Maps.newHashMap());
		sm.worked(10);
		
		taskBuffer = new StringBuffer("Collecting errors after re-factoring.");
		List<Diagnostic> errors = diagnostics.getChildren();
		sm = SubMonitor.convert(pm, taskBuffer.toString(), errors.size());
		if(!errors.isEmpty()) {
			RefactoringStatus status = RefactoringStatus.create(Status.CANCEL_STATUS);
			for(Diagnostic diagnostic : errors) {
				if(diagnostic instanceof AbstractDiagnostic) {
					status.addEntry(new RefactoringStatusEntry(IStatus.ERROR, diagnostic.getMessage()));					
				} 
				sm.worked(1);
			}
			if(status.getEntries().length > 0) {
				return status;				
			}
		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}
	
	@SuppressWarnings("unchecked")
	public RefactoringStatus refactor(IProgressMonitor pm) throws CoreException {
		if(isEnabled()) {
			EObject handleObject = original == null ? originalContainer : original;
			StringBuffer taskBuffer = new StringBuffer("Executing re-factoring for ").append(getName(utility, handleObject));
			SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), IProgressMonitor.UNKNOWN);
			if(originalContainer != null && refactored != null) {
				if(feature.isMany()) {
					Object value = originalContainer.eGet(feature);
					if(value instanceof List<?>) {
						List<EObject> entries = (List<EObject>)value;
						entries.clear();
						EObject refactoredContainer = refactored.eContainer();
						Object refactoredValues = refactoredContainer.eGet(feature);
						if(refactoredValues instanceof EObject) {
							entries.add((EObject)refactoredValues);
						} else {
							entries.addAll((List<EObject>)refactoredValues);														
						}
					}
				} else {
					originalContainer.eSet(feature, refactored);
				}
			} else if(originalContainer != null && original != null) {
				if(feature.isMany()) {
					List<EObject> entries = (List<EObject>)originalContainer.eGet(feature);
					entries.remove(original);
				} else {
					originalContainer.eSet(feature, null);
				}
			} else if(original != null && refactored != null) {
				if(feature.isMany()) {
					EObject container = original.eContainer();
					EList<EObject> entries = (EList<EObject>)container.eGet(feature);
					entries.remove(original);
					entries.add(EcoreUtil.copy(refactored));
				} else {
					Object value = refactored.eGet(feature);
					original.eSet(feature, value == null ? null : EcoreUtil.copy((EObject)value));												
				}
			}
			sm.worked(1);
		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}
	
	public DiffNode getDiffNode() {		
		diffNode = new DiffNode(Differencer.CHANGE);
		EObjectTypedElement left = original == 
				null ? EObjectTypedElement.getEmpty() : 
					new EObjectTypedElement(original, serializer, nameProvider);
		diffNode.setLeft(left);
		EObjectTypedElement right = refactored == 
				null ? EObjectTypedElement.getEmpty() :
					new EObjectTypedElement(refactored, serializer, nameProvider);
		diffNode.setRight(right);
		return diffNode;
	}
	
	@Override
	public Object getModifiedElement() {
		return original;
	}
	
	@Override
	public String getName() {
		EObject refactoringOnObject = original == null ? refactored == null ? originalContainer : refactored : original;
		String refactoringOnType = refactoringOnObject.eClass().getName();
		StringBuffer labelBuffer = new StringBuffer(refactoringOnType);
		String objectName = getName(utility, refactoringOnObject);
		if(!refactoringOnType.equals(objectName)) {
			labelBuffer.append(" ");
			labelBuffer.append(objectName);			
		}
		return "Re-factoring on " + labelBuffer.toString();
	}
	
	private String getName(RefactoringUtility utility, EObject object) {
		IQualifiedNameProvider nameProvider = utility.getInstance(IQualifiedNameProvider.class, object);
		QualifiedName qualifiedName = nameProvider == null ? QualifiedName.create("") : nameProvider.getFullyQualifiedName(object);
		return qualifiedName == null ? object.eClass().getName() : qualifiedName.getLastSegment();
	}
}