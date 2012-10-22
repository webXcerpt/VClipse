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
package org.vclipse.refactoring.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.vclipse.refactoring.ConfigurationProvider;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.IRefactoringExecuter;
import org.vclipse.refactoring.RefactoringStatus;

import com.google.inject.Inject;

public class RefactoringRunner {

	@Inject
	private ConfigurationProvider configuration;
	
	private ChangeRecorder changeRecorder;
	
	public void refactor(IRefactoringContext context) throws CoreException {
		EObject element = context.getSourceElement();
		EObject container = EcoreUtil.getRootContainer(element);
		changeRecorder = new ChangeRecorder(container);
		IRefactoringExecuter refactoring = getRefactoring(element);
		if(refactoring == null) {
			EStructuralFeature feature = context.getStructuralFeature();
			RefactoringStatus status = RefactoringStatus.getExcuterNotAvailable(element, feature);
			throw new CoreException(status);
		} else {
			refactoring.refactor(context);			
		}
	}
	
	public boolean isRefactoringAvailable(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		return getRefactoring(element) != null;
	}
	
	public ChangeRecorder getChangeRecorder() {
		return changeRecorder;
	}
	
	private IRefactoringExecuter getRefactoring(EObject object) {
		EObject rootContainer = EcoreUtil.getRootContainer(object);
		return configuration.getRefactorings().get(rootContainer.eClass());
	}
}
