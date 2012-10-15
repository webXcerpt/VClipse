package org.vclipse.refactoring;

import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class RefactoringStatus extends Status {

	public static RefactoringStatus getInitialisationError(String message) {
		return new RefactoringStatus(Status.ERROR, message);
	}
		
	public static RefactoringStatus getMethodNotAvailable(EObject object, EStructuralFeature feature) {
		StringBuffer messageBuffer = new StringBuffer("Re-factoring for type ");
		messageBuffer.append(object.eClass().getName());
		messageBuffer.append(" and ");
		messageBuffer.append(feature.getName());
		messageBuffer.append(" not available");
		return new RefactoringStatus(Status.ERROR, messageBuffer.toString());
	}
	
	public static RefactoringStatus getExcuterNotAvailable(EObject object, EStructuralFeature feature) {
		StringBuffer messageBuffer = new StringBuffer("Re-factoring executer for type ");
		messageBuffer.append(object.eClass().getName());
		messageBuffer.append(" and ");
		messageBuffer.append(feature.getName());
		messageBuffer.append(" not available");
		return new RefactoringStatus(Status.ERROR, messageBuffer.toString());
	}
	
	public static RefactoringStatus getConfigurationError() {
		String message = "Instantiation of this extension point should contain executable extension part.";
		return new RefactoringStatus(Status.ERROR, message);
	}
	
	public RefactoringStatus(int severity, String message) {
		super(severity, RefactoringPlugin.ID, message);
	}

}
