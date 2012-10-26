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
package org.vclipse.refactoring.ui;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.vclipse.refactoring.IRefactoringUIConfiguration;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.core.RefactoringContext;
import org.vclipse.refactoring.core.RefactoringTask;
import org.vclipse.refactoring.utils.Configuration;
import org.vclipse.refactoring.utils.Extensions;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class UIRefactoringContext extends RefactoringContext implements IRefactoringUIContext {

	@Inject
	private Provider<UIRefactoringContext> contextProvider;
	
	@Inject
	private Configuration configuration;
	
	@Inject
	private Extensions extensions;
	
	private IXtextDocument document;
	
	private List<? extends UserInputWizardPage> pages;
	
	private RefactoringTask refactoringTask;
	
	public UIRefactoringContext() {
		pages = Lists.newArrayList();
	}
	
	public void setRefactoring(RefactoringTask refactoring) {
		this.refactoringTask = refactoring;
	}
	
	@Override
	public void setDocument(IXtextDocument document) {
		this.document = document;
	}
	
	@Override
	public void setPages(List<? extends UserInputWizardPage> pages) {
		this.pages = pages;
	}

	@Override
	public void configureWidgets() {
		EObject rootContainer = EcoreUtil.getRootContainer(getSourceElement());
		IRefactoringUIConfiguration uiConfiguration = extensions.getInstance(IRefactoringUIConfiguration.class, rootContainer);
		uiConfiguration.configureWidgets(this);
	}
	
	@Override
	public IXtextDocument getDocument() {
		return document;
	}

	public RefactoringTask getRefactoring() {
		return refactoringTask;
	}

	@Override
	public List<? extends UserInputWizardPage> getPages() {
		return Collections.unmodifiableList(pages);
	}

	public IRefactoringUIContext copy() {
		UIRefactoringContext context = contextProvider.get();
		for(Entry<Object, Object> entry : getAttributes().entrySet()) {
			context.addAttribute(entry.getKey(), entry.getValue());
		}
		context.setDocument(getDocument());
		context.setPages(getPages());
		context.setSourceElement(getSourceElement());
		context.setType(getType());
		context.setStructuralFeature(getStructuralFeature());
		context.setIndex(getIndex());
		return context;
	}
}
