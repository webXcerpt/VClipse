package org.vclipse.refactoring.core;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.vclipse.refactoring.IPreviewEObjectComputer;
import org.vclipse.refactoring.utils.RefactoringUtility;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

@SuppressWarnings("unchecked")
public class DefaultPreviewEObjectComputer implements IPreviewEObjectComputer {

	protected static final EcorePackage ECORE_PACKAGE = EcorePackage.eINSTANCE;

	@Inject
	private RefactoringUtility utility;
	
	@Override
	public Set<EClass> getTypes() {
		return Sets.newHashSet(ECORE_PACKAGE.getEObject());
	}

	@Override
	public EObject getExisting(EObject container, EObject original, EObject refactored, EStructuralFeature feature) {
		if(original == null) {
			Object value = container.eGet(feature);
			return computePreviewObject(container, value);
		} else {
			if(original.eClass() == refactored.eClass()) {
				return original;
			} else {
				EObject computePreviewObject = computePreviewObject(original);
				if(computePreviewObject == null) {
					Object value = original.eGet(feature);
					return computePreviewObject(original, value);
				}
				return computePreviewObject;
			}
		}
	}

	@Override
	public EObject getRefactored(EObject container, EObject original, EObject refactored, EStructuralFeature feature) {
		if(original != null) {
			EClass typeOriginal = original.eClass();
			EClass typeRefactored = refactored.eClass();
			if(typeOriginal == typeRefactored) {
				return refactored;
			} else {
				Object value = original.eGet(feature);
				if(value instanceof List<?>) {
					EList<EObject> entries = (EList<EObject>)value;
					EObject computePreviewObject = computePreviewObject(refactored, entries);
					return computePreviewObject;
				}
			}
		}
		return computePreviewObject(refactored);
	}
	
	protected EObject computePreviewObject(EObject serachFor, Object value) {
		if(value instanceof EList<?>) {
			EList<EObject> entries = (EList<EObject>)value;
			EObject foundEntry = utility.findEntry(serachFor, entries);
			if(foundEntry != null) {
				return computePreviewObject(foundEntry);					
			}
		} else {
			EObject eobjectValue = (EObject)value;
			return computePreviewObject(eobjectValue);
		}
		return null;
	}
	
	protected EObject computePreviewObject(EObject eobject) {
		Set<EClass> types = getTypes();
		EObject container = eobject;
		while(container != null) {
			EClass containerType = container.eClass();
			if(types.contains(containerType)) {
				return container;
			} else {
				EList<EClass> allSuperTypes = containerType.getEAllSuperTypes();
				for(EClass currentType : allSuperTypes) {
					if(types.contains(currentType)) {
						return container;
					}
				}
			}
			container = eobject.eContainer();
		}
		return eobject;
	}
}
