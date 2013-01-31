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
package org.vclipse.refactoring;

import java.lang.reflect.Method;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.Pair;

public interface IRefactoringExecuter {

	public Set<EClass> getTopLevelTypes();
	
	public void refactor(IRefactoringContext context) throws CoreException;
	
	public void refactor(EObject object) throws CoreException;
	
	public Pair<EObject, Method> getRefactoring(IRefactoringContext context);
}
