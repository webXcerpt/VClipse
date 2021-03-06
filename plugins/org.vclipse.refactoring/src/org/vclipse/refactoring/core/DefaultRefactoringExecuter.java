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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.util.Pair;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.IRefactoringExecuter;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.RefactoringStatus;
import org.vclipse.refactoring.utils.Configuration;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.refactoring.utils.Extensions;
import org.vclipse.refactoring.utils.References;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class DefaultRefactoringExecuter extends MethodCollector implements IRefactoringExecuter {

	public static final String BUTTON_STATE = "button_state";
	public static final String TEXT_FIELD_ENTRY = "text_field_entry";
	public static final String EXISTING_ENTRY_SELECTED = "existing_entry_selected";
	
	public static final String REMOVE_EXISTING_EOBJECT = "remove_existing_object";
	
	public static final String REFACTORING_PREFIX = "refactoring_";
	
	@Inject
	protected References referencesFinder;
	
	@Inject
	protected EntrySearch search;
	
	@Inject
	private Configuration configuration;
	
	@Inject
	protected Extensions extensions;
	
	@Inject
	private Provider<RefactoringContext> contextProvider;
	
	public DefaultRefactoringExecuter() {		
		collect(1, IRefactoringContext.class);
		collect(1, IRefactoringUIContext.class);
		collect(2);
	}
	
	@Override
	public Set<EClass> getTopLevelTypes() {
		return Sets.newHashSet();
	}
	
	public void refactor(IRefactoringContext context) throws CoreException {
		EObject element = context.getSourceElement();
		EObject rootContainer = EcoreUtil.getRootContainer(element);
		EClass containerType = rootContainer.eClass();
		Map<EClassifier, IRefactoringExecuter> refactorings = configuration.getRefactorings();
		IRefactoringExecuter executer = refactorings.get(containerType);
		if(executer == null) {
			EStructuralFeature feature = context.getStructuralFeature();
			RefactoringStatus status = RefactoringStatus.getExcuterNotAvailable(element, feature);
			throw new CoreException(status);
		} else {
			Pair<EObject, Method> refactoringMethod = getRefactoring(context);
			if(refactoringMethod == null) {
				EStructuralFeature feature = context.getStructuralFeature();
				RefactoringStatus status = RefactoringStatus.getMethodNotAvailable(element, feature);
				throw new CoreException(status);
			} else {
				try {
					Method method = refactoringMethod.getSecond();
					List<Object> params = Lists.newArrayList();
					if(method.getParameterTypes().length == 1) {
						params.add(context);
					} else if(method.getParameterTypes().length == 2) {
						params.add(context);
						EObject invokeOn = refactoringMethod.getFirst();
						params.add(invokeOn);
					}
					method.invoke(executer, params.toArray());
				} catch (Exception exception) {
					String message = exception.getMessage();
					RefactoringPlugin.log(IStatus.ERROR, message);
				}
			}
		}
	}
	
	public void refactor(EObject object) throws CoreException {
		RefactoringContext context = contextProvider.get();
		context.setSourceElement(object);
		context.setType(RefactoringType.Replace);
		refactor(context);
	}
	
	public Pair<EObject, Method> getRefactoring(IRefactoringContext context) {
		String prefix = REFACTORING_PREFIX + context.getType();
		EObject element = context.getSourceElement();
		EStructuralFeature feature = context.getStructuralFeature();
		return getMethod(element, feature, prefix);
	}
}
