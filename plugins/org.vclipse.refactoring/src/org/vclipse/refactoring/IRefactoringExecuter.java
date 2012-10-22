package org.vclipse.refactoring;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

public interface IRefactoringExecuter {

	public EPackage getPackage();
	
	public Set<EClass> getTopLevelTypes();
	
	public void refactor(IRefactoringContext context) throws CoreException;
	
	public void refactor(EObject object) throws CoreException;
	
}
