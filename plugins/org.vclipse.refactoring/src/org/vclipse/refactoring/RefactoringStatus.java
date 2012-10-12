package org.vclipse.refactoring;

import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class RefactoringStatus extends Status {

	public static RefactoringStatus getMethodNotAvailable(EObject object, EStructuralFeature feature) {
		String message = 
				"Re-factoring for type " + object.eClass().getName() + 
					" and " + feature.getName() + " not available";
		return new RefactoringStatus(Status.ERROR, message);
	}
	
	public static RefactoringStatus getExcuterNotAvailable(EObject object, EStructuralFeature feature) {
		String message = 
				"Re-factoring executer for type " + object.eClass().getName() +
					" and " + feature.getName() + " not available";
		return new RefactoringStatus(Status.ERROR, message);
	}
	
	public static RefactoringStatus getConfigurationError() {
		String message = "Instantiation of this extension point should contain executable extension part.";
		return new RefactoringStatus(Status.ERROR, message);
	}
	
	public RefactoringStatus(int severity, String message) {
		super(severity, RefactoringPlugin.ID, message);
	}

}
