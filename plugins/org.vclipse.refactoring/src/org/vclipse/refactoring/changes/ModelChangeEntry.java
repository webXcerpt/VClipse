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
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusEntry;
import org.eclipse.xtext.diagnostics.AbstractDiagnostic;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.serializer.ISerializer;
import org.vclipse.base.compare.MultipleEntriesTypedElement;
import org.vclipse.refactoring.IPreviewObjectComputer;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.core.DiffNode;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.refactoring.utils.Extensions;
import org.vclipse.refactoring.utils.Labels;

import com.google.common.collect.Maps;
import com.google.inject.Injector;

public class ModelChangeEntry extends Change {

	private EObject existing, refactored;
	private FeatureChange change;
	
	private Extensions extensions;
	private EntrySearch search;
	private Labels labels;
	
	private EValidator validator;
	private IQualifiedNameProvider nameProvider;
	private ISerializer serializer;
	
	public void addChange(EObject existing, EObject refactored, FeatureChange change) {
		if(extensions == null) {
			init();
		}
		this.existing = existing;
		this.refactored = refactored;
		this.change = change;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Change perform(IProgressMonitor pm) throws CoreException {
		if(isEnabled()) {
			EObject handleWithObject = existing == null ? refactored : existing;
			String name = labels.getSimpleName(handleWithObject);
			StringBuffer taskBuffer = new StringBuffer("Executing re-factoring for ").append(name);
			SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), IProgressMonitor.UNKNOWN);
			EStructuralFeature feature = change.getFeature();
			EList<ListChange> listChanges = change.getListChanges();
			if(listChanges.isEmpty()) {
				EObject existingContainer = existing.eContainer();
				EObject refactoredContainer = refactored.eContainer();
				if(existingContainer == refactoredContainer) {
					existing.eSet(feature, refactored.eGet(feature));
				} else if(search.equallyTyped(existing, refactored) && search.equallyNamed(existing, refactored)) {
					Object value = refactored.eGet(feature);
					existing.eSet(feature, value);
				} else if(search.equallyTyped(existingContainer, refactoredContainer) || search.equallyNamed(existingContainer, refactoredContainer)) {
					Object value = refactored.eGet(feature);
					existing.eSet(feature, value);
				}
			} else {
				int decrement = 0;
				for(ListChange listChange : listChanges) {
					ChangeKind kind = listChange.getKind();
					int index = listChange.getIndex();
					index = index == 0 ? index : (index - decrement);
					EList<EObject> originalEntries = (EList<EObject>)existing.eGet(feature);
					EList<EObject> refactoredEntries = (EList<EObject>)refactored.eGet(feature);
					if(ChangeKind.ADD_LITERAL == kind) {
						decrement += 1;
						originalEntries.remove(index);							
					} else if(ChangeKind.REMOVE_LITERAL == kind) {
						decrement = 0;
						EObject entry = refactoredEntries.get(index);
						originalEntries.add(entry);
					} else {
						EObject moved = refactoredEntries.get(index);
						EObject found = search.findEntry(moved, originalEntries);
						originalEntries.move(index, found);
					}
				}
			}
			sm.done();
		}
		return null;
	}
	
	@Override
	public void initializeValidationData(IProgressMonitor pm) {
		EObject handleWithObject = existing == null ? refactored : existing;
		if(handleWithObject == null) {
			StringBuffer errorBuffer = new StringBuffer("Can not initialize validation data for a change. ");
			errorBuffer.append("Both existing and refactored objects are null.");
			throw new IllegalArgumentException(errorBuffer.toString());
		}
		String name = labels.getSimpleName(handleWithObject);
		StringBuffer taskBuffer = new StringBuffer("Initializing change for ");
		taskBuffer.append(name);
		SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), IProgressMonitor.UNKNOWN);
		sm.beginTask(taskBuffer.toString(), IProgressMonitor.UNKNOWN);
		Registry registry = extensions.getInstance(EValidator.Registry.class, handleWithObject);
		EPackage epackage = handleWithObject.eClass().getEPackage();
		validator = registry.getEValidator(epackage);
		nameProvider = extensions.getInstance(IQualifiedNameProvider.class, handleWithObject);
		serializer = extensions.getInstance(ISerializer.class, handleWithObject);
		sm.done();
	}

	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		RefactoringStatus refactoringStatus = RefactoringStatus.create(Status.OK_STATUS);
		EObject handleWithObject = existing == null ? refactored : existing;
		String name = labels.getSimpleName(handleWithObject);
		StringBuffer taskBuffer = new StringBuffer("Validating change for ").append(name);
		SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), 10);
		BasicDiagnostic diagnostics = new BasicDiagnostic();
		validator.validate(handleWithObject, diagnostics, Maps.newHashMap());
		sm.done();
		
		taskBuffer = new StringBuffer("Collecting errors after re-factoring.");
		List<Diagnostic> errors = diagnostics.getChildren();
		sm = SubMonitor.convert(pm, taskBuffer.toString(), errors.size());
		if(!errors.isEmpty()) {
			refactoringStatus = RefactoringStatus.create(Status.CANCEL_STATUS);
			for(Diagnostic diagnostic : errors) {
				if(diagnostic instanceof AbstractDiagnostic) {
					refactoringStatus.addEntry(new RefactoringStatusEntry(IStatus.ERROR, diagnostic.getMessage()));					
				} 
				sm.worked(1);
			}
			sm.done();
		}
		return refactoringStatus;
	}
	
	public DiffNode getDiffNode() {		
		DiffNode diffNode = new DiffNode();
		EObject handleWithObject = existing == null ? refactored : existing;
		IPreviewObjectComputer previewComputer = extensions.getInstance(IPreviewObjectComputer.class, handleWithObject);	
		List<EObject> existingPreview = previewComputer.getExisting(existing, refactored, change);
		if(existingPreview == null) {
			MultipleEntriesTypedElement typedExisting = MultipleEntriesTypedElement.getDefault();
			diffNode.setLeft(typedExisting);
		} else {
			EObject[] elements = existingPreview.toArray(new EObject[existingPreview.size()]);
			MultipleEntriesTypedElement typedExisting = new MultipleEntriesTypedElement(serializer, nameProvider, elements);
			diffNode.setLeft(typedExisting);
		}
		List<EObject> refactoredPreview = previewComputer.getRefactored(existing, refactored, change);
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
		return labels.getPreviewLabel(existing == null ? refactored : existing);
	}
	
	private void init() {
		Injector injector = RefactoringPlugin.getInstance().getInjector();
		extensions = injector.getInstance(Extensions.class);
		search = injector.getInstance(EntrySearch.class);
		labels = injector.getInstance(Labels.class);
	}
}
