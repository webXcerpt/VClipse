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
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.util.Pair;
import org.vclipse.base.BasePlugin;
import org.vclipse.refactoring.ExtensionsReader;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class RefactoringRunner {

	@Inject
	private ExtensionsReader reader;
	
	@SuppressWarnings("unchecked")
	public List<EObject> refactor(IRefactoringContext context) {
		List<EObject> changes = Lists.<EObject>newArrayList();
		EObject sourceElement = context.getSourceElement();
		Refactoring refactoring = getRefactoring(sourceElement);
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
					Object result = pair.getSecond().invoke(refactoring, params.toArray());
					changes.addAll((List<EObject>)result);
				} catch (Exception exception) {
					BasePlugin.log(exception.getMessage(), exception);
				}
			}
		}
		return changes;	
	}
	
	public void refactor(EObject object) {
		IRefactoringContext context = new RefactoringContext();
		context.setSourceElement(object);
		context.setType(RefactoringType.Replace);
		refactor(context);
	}
	
	public boolean isRefactoringAvailable(IRefactoringContext context) {
		return getRefactoring(context.getSourceElement()) != null;
	}
	
	private Refactoring getRefactoring(EObject object) {
		EObject rootContainer = EcoreUtil.getRootContainer(object);
		Iterator<Refactoring> iterator = reader.getRefactorings().get(rootContainer.eClass()).iterator();
		if(iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
}
