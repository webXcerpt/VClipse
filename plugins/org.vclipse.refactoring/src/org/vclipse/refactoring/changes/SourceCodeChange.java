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
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.EValidator.Registry;
import org.eclipse.emf.ecore.change.ChangeKind;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.eclipse.emf.ecore.change.ListChange;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusEntry;
import org.eclipse.xtext.diagnostics.AbstractDiagnostic;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.serializer.ISerializer;
import org.vclipse.base.VClipseStrings;
import org.vclipse.refactoring.IPreviewObjectComputer;
import org.vclipse.refactoring.compare.MultipleEntriesTypedElement;
import org.vclipse.refactoring.core.DiffNode;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.refactoring.utils.Extensions;

import com.google.common.collect.Maps;

public class SourceCodeChange extends NoChange {

	private Extensions extensions;
	private EntrySearch search;
	
	private EValidator validator;
	private IQualifiedNameProvider nameProvider;
	private ISerializer serializer;
	
	private EObject existing;
	private EObject refactored;
	private FeatureChange featureChange;
	
	private EObject rootOriginal;
	private EObject rootRefactored;
	
	private List<EObject> rootContents;
	private List<EObject> refactoredContents;
	
	public SourceCodeChange(EObject rootOriginal, EObject rootRefactored, Extensions extensions, EntrySearch search) {
		this.extensions = extensions;
		this.search = search;
		
		this.rootOriginal = rootOriginal;
		this.rootRefactored = rootRefactored;
		
		rootContents = search.getContents(rootOriginal);
		refactoredContents = search.getContents(rootRefactored);
	}
	
	public void addChange(EObject existing, EObject refactored, FeatureChange featureChange) {
		this.existing = existing;
		this.refactored = refactored;
		this.featureChange = featureChange;
		
		EObject initObject = existing == null ? refactored : existing;
		Registry registry = extensions.getInstance(EValidator.Registry.class, initObject);
		EPackage epackage = initObject.eClass().getEPackage();
		validator = registry.getEValidator(epackage);
		nameProvider = extensions.getInstance(IQualifiedNameProvider.class, initObject);
		serializer = extensions.getInstance(ISerializer.class, initObject);
	}
	
	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		EObject handleWithObject = existing == null ? refactored : existing;
		String name = getName(extensions, handleWithObject);
		StringBuffer taskBuffer = new StringBuffer("Validating change for ").append(name);
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
	public RefactoringStatus applyRefactoring(IProgressMonitor pm) throws CoreException {
		if(isEnabled()) {
			EObject handleWithObject = existing == null ? refactored : existing;
			String name = getName(extensions, handleWithObject);
			StringBuffer taskBuffer = new StringBuffer("Executing re-factoring for ").append(name);
			SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), IProgressMonitor.UNKNOWN);
			EStructuralFeature feature = featureChange.getFeature();
			EList<ListChange> listChanges = featureChange.getListChanges();
			if(listChanges.isEmpty()) {
				if(search.equallyTypedContainer(existing, refactored)) {
					if(search.equallyTyped(existing, refactored)) {
						mergeEquallyTyped();
					} else {
						EObject existingContainer = existing.eContainer();
						EReference existingContainment = existing.eContainmentFeature();
						if(existingContainment.isMany()) {
							List<EObject> entries = (List<EObject>)existingContainer.eGet(existingContainment);
							entries.remove(existing);
						} else {
							existingContainer.eSet(existingContainment, refactored);						
						}	
					}
				} else if(existing.eContainer() == null && refactored.eContainer() == null) {
					existing.eSet(feature, refactored.eGet(feature));
				} else {
					if(search.equallyTyped(existing, refactored)) {
						mergeEquallyTyped();
					} else {
						EObject refactoredContainer = refactored.eContainer();
						EObject foundEntry = search.findEntry(refactoredContainer == null ? refactored : refactoredContainer, rootContents);
						foundEntry.eSet(refactored.eContainmentFeature(), refactored);
					}
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
			sm.worked(1);
		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}
	
	public DiffNode getDiffNode() {		
		DiffNode diffNode = new DiffNode();
		EObject handleWithObject = existing == null ? refactored : existing;
		IPreviewObjectComputer previewComputer = extensions.getInstance(IPreviewObjectComputer.class, handleWithObject);	
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
	
	private String getName(Extensions extensions, EObject object) {
		IQualifiedNameProvider nameProvider = extensions.getInstance(IQualifiedNameProvider.class, object);
		QualifiedName qualifiedName = nameProvider == null ? QualifiedName.create("") : nameProvider.getFullyQualifiedName(object);
		return qualifiedName == null ? object.eClass().getName() : qualifiedName.getLastSegment();
	}

	@SuppressWarnings("unchecked")
	private void mergeEquallyTyped() {
		EObject container = existing.eContainer();
		EStructuralFeature containment = existing.eContainmentFeature();
		if(containment.isMany()) {
			List<EObject> entries = (List<EObject>)container.eGet(containment);
			int index = entries.indexOf(existing);
			entries.remove(existing);
			entries.add(index, refactored);
		} else {
			container.eSet(containment, EcoreUtil.copy(refactored));							
		}
	}
}