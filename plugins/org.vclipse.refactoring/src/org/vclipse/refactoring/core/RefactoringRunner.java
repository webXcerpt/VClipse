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

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.util.Pair;
import org.vclipse.base.BasePlugin;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.configuration.ExtensionsReader;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class RefactoringRunner {

	@Inject
	private ExtensionsReader reader;
	
	private ChangeRecorder changeRecorder;
	
	public void refactor(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		EObject container = EcoreUtil.getRootContainer(element);
		changeRecorder = new ChangeRecorder(container);
		RefactoringExecuter refactoring = getRefactoring(element);
		if(refactoring != null) {
			Pair<EObject, Method> pair = refactoring.getRefactoring(context);
			if(pair != null) {
				try {
					Method method = pair.getSecond();
					List<Object> params = Lists.newArrayList();
					if(method.getParameterTypes().length == 1) {
						params.add(context);
					} else if(method.getParameterTypes().length == 2) {
						params.add(context);
						params.add(pair.getFirst());
					}
					pair.getSecond().invoke(refactoring, params.toArray());
				} catch (Exception exception) {
					BasePlugin.log(exception.getMessage(), exception);
				}
			}
		}
	}
	
	public void refactor(EObject object) {
		refactor(RefactoringContext.create(object, null, RefactoringType.Replace));
	}
	
	public boolean isRefactoringAvailable(IRefactoringContext context) {
		RefactoringExecuter executerExists = getRefactoring(context.getSourceElement());
		return executerExists.getRefactoring(context) != null;
	}
	
	protected ChangeRecorder getChangeRecorder() {
		return changeRecorder;
	}
	
	private RefactoringExecuter getRefactoring(EObject object) {
		EObject rootContainer = EcoreUtil.getRootContainer(object);
		return reader.getRefactorings().get(rootContainer.eClass());
	}
}
