package org.vclipse.refactoring;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

public interface IRefactoringExecuter {

	public Set<EClass> getTopLevelTypes();
	
	public void refactor(IRefactoringContext context) throws CoreException;
	
	public void refactor(EObject object) throws CoreException;
	
}
