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

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.change.ChangeFactory;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder;
import org.eclipse.xtext.util.Pair;
import org.vclipse.base.BasePlugin;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.configuration.ExtensionsReader;
import org.vclipse.refactoring.ui.RefactoringUtility;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public abstract class RefactoringExecuter extends MethodCollector {

	public static String BUTTON_STATE = "button_state";
	public static String TEXT_FIELD_ENTRY = "text_field_entry";
	
	public static final String REFACTORING_PREFIX = "refactoring_";
	
	protected ChangeFactory changeFactory;
	
	private ChangeRecorder changeRecorder;
	
	@Inject
	protected NodeModelBuilder nodeModelBuilder;
	
	@Inject
	protected ReferenceFinderExtension referencesFinder;
	
	@Inject
	protected RefactoringUtility refactoringUtility;
	
	@Inject
	private ExtensionsReader reader;
	
	public RefactoringExecuter() {		
		collect(1, IRefactoringContext.class);
		collect(2);
		changeFactory = ChangeFactory.eINSTANCE;
	}
	
	public void refactor(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		EObject container = EcoreUtil.getRootContainer(element);
		changeRecorder = new ChangeRecorder(container);
		
		RefactoringExecuter refactoring = getRefactoring(element);
		if(refactoring != null) {
			Pair<EObject, Method> refactoringMethod = getRefactoring(context);
			if(refactoringMethod != null) {
				try {
					Method method = refactoringMethod.getSecond();
					List<Object> params = Lists.newArrayList();
					if(method.getParameterTypes().length == 1) {
						params.add(context);
					} else if(method.getParameterTypes().length == 2) {
						params.add(context);
						params.add(refactoringMethod.getFirst());
					}
					refactoringMethod.getSecond().invoke(refactoring, params.toArray());
				} catch (Exception exception) {
					BasePlugin.log(exception.getMessage(), exception);
				}
			} else {
				System.err.println("refactoring method was null");
			}
		} else {
			System.err.println("refactoring for " + element + " was null");
		}
		
		String prefix = REFACTORING_PREFIX + context.getType();
		Pair<EObject, Method> pair = getMethod(element, context.getStructuralFeature(), prefix);
		if(pair != null) {
			try {
				pair.getSecond().invoke(this, new Object[]{context, pair.getFirst()});
			} catch (Exception exception) {
				RefactoringPlugin.log(exception.getMessage(), exception);
			}
		} 
	}
	
	public void refactor(EObject object) {
		refactor(RefactoringContext.create(object, null, RefactoringType.Replace));
	}
	
	protected ChangeRecorder getChangeRecorder() {
		return changeRecorder;
	}
	
	public boolean isRefactoringAvailable(IRefactoringContext context) {
		return getRefactoring(context.getSourceElement()) != null;
	}
	
	private RefactoringExecuter getRefactoring(EObject object) {
		EObject rootContainer = EcoreUtil.getRootContainer(object);
		return reader.getRefactorings().get(rootContainer.eClass());
	}
	
	public Pair<EObject, Method> getRefactoring(IRefactoringContext context) {
		String prefix = REFACTORING_PREFIX + context.getType();
		return getMethod(context.getSourceElement(), context.getStructuralFeature(), prefix);
	}
	
	@SuppressWarnings("unchecked")
	public EList<EObject> getEObjectList(List<?> elements) {
		return (EList<EObject>)elements;
	}
	
	public EList<EObject> copy(EList<? extends EObject> elements) {
		return new BasicEList<EObject>(EcoreUtil2.copyAll(elements));
	}
}
