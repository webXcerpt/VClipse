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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.configuration.ConfigurationProvider;

import com.google.inject.Inject;

public class RefactoringRunner {

	@Inject
	private ConfigurationProvider configuration;
	
	private ChangeRecorder changeRecorder;
	
	public void refactor(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		EObject container = EcoreUtil.getRootContainer(element);
		changeRecorder = new ChangeRecorder(container);
		RefactoringExecuter refactoring = getRefactoring(element);
		if(refactoring != null) {
			refactoring.refactor(context);
		} else {
			System.err.println("Could not find re-factoring for type " + element.eClass());
		}
	}
	
	public boolean isRefactoringAvailable(IRefactoringContext context) {
		RefactoringExecuter executerExists = getRefactoring(context.getSourceElement());
		return executerExists.getRefactoring(context) != null;
	}
	
	public ChangeRecorder getChangeRecorder() {
		return changeRecorder;
	}
	
	private RefactoringExecuter getRefactoring(EObject object) {
		EObject rootContainer = EcoreUtil.getRootContainer(object);
		return configuration.getRefactorings().get(rootContainer.eClass());
	}
}
