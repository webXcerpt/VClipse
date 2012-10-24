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

import java.util.Collection;
import java.util.List;

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
import org.eclipse.emf.ecore.change.ChangeKind;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.eclipse.emf.ecore.change.ListChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusEntry;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.diagnostics.AbstractDiagnostic;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.serializer.ISerializer;
import org.vclipse.base.VClipseStrings;
import org.vclipse.refactoring.IPreviewObjectComputer;
import org.vclipse.refactoring.compare.MultipleEntriesTypedElement;
import org.vclipse.refactoring.core.DiffNode;
import org.vclipse.refactoring.utils.RefactoringUtility;

import com.google.common.collect.Maps;

public class SourceCodeChange extends NoChange {

	private RefactoringUtility utility;
	
	private EValidator validator;
	private IQualifiedNameProvider nameProvider;
	private ISerializer serializer;
	
	private EObject existing;
	private EObject refactored;
	private FeatureChange featureChange;
	
	public SourceCodeChange(RefactoringUtility utility) {
		this.utility = utility;
	}
	
	public void addChange(EObject existing, EObject refactored, FeatureChange featureChange) {
		this.existing = existing;
		this.refactored = refactored;
		this.featureChange = featureChange;
		
		EObject initObject = existing == null ? refactored : existing;
		Registry registry = utility.getInstance(EValidator.Registry.class, initObject);
		EPackage epackage = initObject.eClass().getEPackage();
		validator = registry.getEValidator(epackage);
		nameProvider = utility.getInstance(IQualifiedNameProvider.class, initObject);
		serializer = utility.getInstance(ISerializer.class, initObject);
	}
	
	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		EObject handleWithObject = existing == null ? refactored : existing;
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
			EObject handleObject = existing == null ? refactored : existing;
			StringBuffer taskBuffer = new StringBuffer("Executing re-factoring for ").append(getName(utility, handleObject));
			SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), IProgressMonitor.UNKNOWN);
			EStructuralFeature feature = featureChange.getFeature();
			EList<ListChange> listChanges = featureChange.getListChanges();
			if(listChanges.isEmpty()) {
				Object value = refactored.eGet(feature);
				if(value instanceof EObject) {
					existing.eSet(feature, EcoreUtil2.copy((EObject)value));					
				} else if(value instanceof EList<?>) {
					Collection<EObject> listCopy = EcoreUtil2.copyAll((EList<EObject>)value);
					existing.eSet(feature, listCopy);
				} else {
					existing.eSet(feature, null);
				}
			} else {
				for(ListChange listChange : listChanges) {
					ChangeKind kind = listChange.getKind();
					int index = listChange.getIndex();
					EList<EObject> originalEntries = (EList<EObject>)existing.eGet(feature);
					EList<EObject> refactoredEntries = (EList<EObject>)refactored.eGet(feature);
					if(ChangeKind.ADD_LITERAL == kind) {
						originalEntries.remove(index);							
					} else if(ChangeKind.REMOVE_LITERAL == kind) {
						EObject entry = refactoredEntries.get(index);
						originalEntries.add(EcoreUtil2.copy(entry));
					} else if(ChangeKind.MOVE_LITERAL == kind) {
						System.err.println("move literal");
					}
				}
			}
			sm.worked(1);
		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}
	
	public DiffNode getDiffNode() {		
		DiffNode diffNode = new DiffNode();
		IPreviewObjectComputer previewComputer = utility.getInstance(IPreviewObjectComputer.class, existing == null ? refactored : existing);	
		List<EObject> existingPreview = previewComputer.getExisting(existing, refactored, featureChange);
		if(existingPreview == null) {
			MultipleEntriesTypedElement typedExisting = MultipleEntriesTypedElement.getDefault();
			diffNode.setLeft(typedExisting);
		} else {
			EObject[] elements = existingPreview.toArray(new EObject[existingPreview.size()]);
			MultipleEntriesTypedElement typedExisting = new MultipleEntriesTypedElement(serializer, nameProvider, elements);
			diffNode.setLeft(typedExisting);
		}
		List<EObject> refactoredPreview = previewComputer.getRefactored(existing, refactored, featureChange);
		if(refactoredPreview == null) {
			MultipleEntriesTypedElement typedRefactored = MultipleEntriesTypedElement.getDefault();
			diffNode.setRight(typedRefactored);
		} else {
			EObject[] elements = refactoredPreview.toArray(new EObject[refactoredPreview.size()]);
			MultipleEntriesTypedElement typedRefactored = new MultipleEntriesTypedElement(serializer, nameProvider, elements);
			diffNode.setRight(typedRefactored);
		}
		return diffNode;
	}
	
	@Override
	public Object getModifiedElement() {
		return existing == null ? refactored : existing;
	}
	
	@Override
	public String getName() {
		EObject refactoringOnObject = existing == null ? refactored : existing;
		String refactoringOnType = refactoringOnObject.eClass().getName();
		List<String> parts = VClipseStrings.splitCamelCase(refactoringOnType);
		StringBuffer labelBuffer = new StringBuffer("Re-factoring on ");
		int size = parts.size() - 1;
		for(String part : parts) {
			labelBuffer.append(part.toLowerCase());
			if(parts.indexOf(part) != size) {
				labelBuffer.append(" ");
			}
		}
		return labelBuffer.toString();
	}
	
	private String getName(RefactoringUtility utility, EObject object) {
		IQualifiedNameProvider nameProvider = utility.getInstance(IQualifiedNameProvider.class, object);
		QualifiedName qualifiedName = nameProvider == null ? QualifiedName.create("") : nameProvider.getFullyQualifiedName(object);
		return qualifiedName == null ? object.eClass().getName() : qualifiedName.getLastSegment();
	}
}