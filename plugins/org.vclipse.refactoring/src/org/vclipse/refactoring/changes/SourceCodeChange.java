package org.vclipse.refactoring.changes;

import java.io.IOException;
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
import org.eclipse.emf.ecore.change.ChangeKind;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.eclipse.emf.ecore.change.ListChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusEntry;
import org.eclipse.xtext.diagnostics.AbstractDiagnostic;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.serializer.ISerializer;
import org.vclipse.base.ui.compare.EObjectTypedElement;
import org.vclipse.refactoring.changes.SourceCodeChanges.InputStreamProvider;
import org.vclipse.refactoring.core.DiffNode;
import org.vclipse.refactoring.utils.RefactoringUtility;

import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;

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
		StringBuffer taskBuffer = new StringBuffer("Validating change for ").append(getName(utility, refactored));
		SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), 10);
		BasicDiagnostic diagnostics = new BasicDiagnostic();
		validator.validate(refactored, diagnostics, Maps.newHashMap());
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
			StringBuffer taskBuffer = new StringBuffer("Executing re-factoring for ").append(getName(utility, original));
			SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), featureChanges.size());
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
								if(ChangeKind.REMOVE_LITERAL == listChange.getKind()) {
									originalEntries.remove(index);									
								} else if(ChangeKind.ADD_LITERAL == listChange.getKind()) {
									EObject entry = refactoredEntries.get(index);								
									originalEntries.add(entry);
								} else if(ChangeKind.MOVE_LITERAL == listChange.getKind()) {
									EObject entry = originalEntries.get(index);								
									originalEntries.set(index, entry);
								}
							}
						}					
					}
				} else {
					original.eSet(feature, refactoredValue);
				}
				sm.worked(1);
			}
		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}
	
	public DiffNode getDiffNode() {		
		if(diffNode == null) {
			diffNode = new DiffNode(Differencer.CHANGE);
			EObjectTypedElement left = original == null ? EObjectTypedElement.getEmpty() : new EObjectTypedElement(original, serializer, nameProvider);
			diffNode.setLeft(left);
			EObjectTypedElement right = new EObjectTypedElement(refactored, serializer, nameProvider);
			diffNode.setRight(right);		
		}
		return diffNode;
	}
	
	protected boolean isEmpty() {
		try {
			InputStreamProvider empty = SourceCodeChanges.InputStreamProvider.getEmpty();
			InputStreamProvider diff = SourceCodeChanges.InputStreamProvider.getInstance(getDiffNode());
			return ByteStreams.equal(empty, diff);
		} catch (IOException e) {
			return false;
		}
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