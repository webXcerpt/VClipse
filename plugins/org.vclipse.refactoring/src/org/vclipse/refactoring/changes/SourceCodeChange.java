package org.vclipse.refactoring.changes;

import java.util.List;
import java.util.Map;

import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.EValidator.Registry;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.eclipse.emf.ecore.change.ListChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.serializer.ISerializer;
import org.vclipse.base.ui.compare.EObjectTypedElement;
import org.vclipse.refactoring.core.DiffNode;
import org.vclipse.refactoring.utils.RefactoringUtility;

import com.google.common.collect.Maps;

public class SourceCodeChange extends NoChange {

	private EObject original;
	private EObject refactored;
	private EList<FeatureChange> featureChanges;
		
	private DiffNode diffNode;
	
	private EValidator validator;
	private IQualifiedNameProvider nameProvider;
	private ISerializer serializer;
	
	private RefactoringUtility utility;
	
	public SourceCodeChange(RefactoringUtility utility, EObject original, EObject refactored, EList<FeatureChange> featureChanges) {
		this.original = original;
		this.refactored = refactored;
		this.featureChanges = featureChanges;
		this.utility = utility;
		
		Registry registry = utility.getInstance(EValidator.Registry.class, original);
		EPackage epackage = original.eClass().getEPackage();
		validator = registry.getEValidator(epackage);
		nameProvider = utility.getInstance(IQualifiedNameProvider.class, original);
		serializer = utility.getInstance(ISerializer.class, original);
	}
	
	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		pm.beginTask("Validating change for " + getName(utility, original), IProgressMonitor.UNKNOWN);
		BasicDiagnostic diagnostics = new BasicDiagnostic();
		Map<Object, Object> validationContext = Maps.newHashMap();
		validator.validate(original, diagnostics, validationContext);
		for(Diagnostic diagnostic : diagnostics.getChildren()) {
			String message = diagnostic.getMessage();
			RefactoringStatus status = RefactoringStatus.createErrorStatus(message);
			return status;
		}
		
		validator.validate(refactored, diagnostics, validationContext);
		for(Diagnostic diagnostic : diagnostics.getChildren()) {
			String message = diagnostic.getMessage();
			RefactoringStatus status = RefactoringStatus.createErrorStatus(message);
			return status;
		}
		pm.done();
		return RefactoringStatus.create(Status.OK_STATUS);
	}
	
	@SuppressWarnings("unchecked")
	public RefactoringStatus refactor(IProgressMonitor pm) throws CoreException {
		if(isEnabled()) {
			pm.beginTask("Executing re-factoring for " + getName(utility, original), IProgressMonitor.UNKNOWN);
			for(FeatureChange featureChange : featureChanges) {
				EStructuralFeature feature = featureChange.getFeature();
				Object refactoredValue = refactored.eGet(feature);
				if(feature.isMany()) {
					Object originalValue = original.eGet(feature);
					if(feature.isMany()) {
						List<EObject> refactoredEntries = (List<EObject>)refactoredValue;
						List<EObject> originalEntries = (List<EObject>)originalValue;
						EList<ListChange> listChanges = featureChange.getListChanges();
						if(!listChanges.isEmpty()) {
							for(ListChange listChange : listChanges) {
								int index = listChange.getIndex();
								if(refactoredEntries.size() > originalEntries.size()) {
									EObject entry = refactoredEntries.get(index);								
									originalEntries.add(entry);
								} else if(refactoredEntries.size() < originalEntries.size()) {
									originalEntries.remove(index);
								}
							}
						}					
					}
				} else {
					original.eSet(feature, refactoredValue);
				}
			}
			pm.done();
		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}
	
	public DiffNode getDiffNode() {		
		diffNode = new DiffNode(Differencer.CHANGE);
		EObjectTypedElement left = 
				original == null ? EObjectTypedElement.getEmpty() : new EObjectTypedElement(original, serializer, nameProvider);
		
		diffNode.setLeft(left);
		
		EObjectTypedElement right = 
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
		return "Re-factoring for " + getName(utility, original);
	}
	
	private String getName(RefactoringUtility utility, EObject object) {
		IQualifiedNameProvider nameProvider = utility.getInstance(IQualifiedNameProvider.class, object);
		QualifiedName qualifiedName = nameProvider == null ? QualifiedName.create("") : nameProvider.getFullyQualifiedName(object);
		return qualifiedName == null ? object.eClass().getName() : qualifiedName.getLastSegment();
	}
}