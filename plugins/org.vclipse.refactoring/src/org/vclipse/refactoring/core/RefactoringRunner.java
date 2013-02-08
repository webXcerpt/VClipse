/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.refactoring.core;

import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.util.Pair;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.IRefactoringExecuter;
import org.vclipse.refactoring.RefactoringStatus;
import org.vclipse.refactoring.utils.Configuration;

import com.google.inject.Inject;

public class RefactoringRunner {

	@Inject
	private Configuration configuration;
	
	@Inject
	private RefactoringStatus refactoringStatus;
	
	private ChangeRecorder changeRecorder;
	
	public void refactor(IRefactoringContext context) throws CoreException {
		EObject element = context.getSourceElement();
		ResourceSet resourceSet = element.eResource().getResourceSet();
		changeRecorder = new ChangeRecorder(resourceSet);
		IRefactoringExecuter refactoring = getRefactoring(element);
		if(refactoring == null) {
			EStructuralFeature feature = context.getStructuralFeature();
			RefactoringStatus status = refactoringStatus.getExcuterNotAvailable(element, feature);
			throw new CoreException(status);
		} else {
			refactoring.refactor(context);			
		}
	}
	
	public boolean isRefactoringAvailable(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		IRefactoringExecuter executer = getRefactoring(element);
		Pair<EObject, Method> executerForContext = executer.getRefactoring(context);
		return executerForContext != null;
	}
	
	public ChangeRecorder getChangeRecorder() {
		return changeRecorder;
	}
	
	private IRefactoringExecuter getRefactoring(EObject object) {
		EObject rootContainer = EcoreUtil.getRootContainer(object);
		Map<EClassifier, IRefactoringExecuter> refactorings = configuration.getRefactorings();
		EClass rootType = rootContainer.eClass();
		IRefactoringExecuter executer = refactorings.get(rootType);
		return executer;
	}
}
