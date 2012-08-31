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
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.vclipse.refactoring.ExtensionsReader;
import org.vclipse.refactoring.core.RefactoringContext;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class UIRefactoringContext extends RefactoringContext implements IUIRefactoringContext {

	@Inject
	private Provider<UIRefactoringContext> contextProvider;
	
	@Inject
	private ExtensionsReader reader;
	
	private IXtextDocument document;
	
	private List<? extends UserInputWizardPage> pages;
	
	public UIRefactoringContext() {
		pages = Lists.newArrayList();
	}
	
	@Override
	public IXtextDocument getDocument() {
		return document;
	}

	@Override
	public void setDocument(IXtextDocument document) {
		this.document = document;
	}

	@Override
	public void handleWidgets() {
		EObject rootContainer = EcoreUtil.getRootContainer(getSourceElement());
		EClass eClass = rootContainer.eClass();
		Iterator<RefactoringUICustomisation> iterator = reader.getUICustomisation().get(eClass).iterator();
		if(iterator.hasNext()) {
			iterator.next().switchWidgets(this);
		}
	}

	@Override
	public List<? extends UserInputWizardPage> getPages() {
		return Collections.unmodifiableList(pages);
	}

	@Override
	public void setPages(List<? extends UserInputWizardPage> pages) {
		this.pages = pages;
	}

	public IUIRefactoringContext copy() {
		UIRefactoringContext context = contextProvider.get();
		for(Entry<Object, Object> entry : getAttributes().entrySet()) {
			context.addAttribute(entry.getKey(), entry.getValue());
		}
		context.setDocument(getDocument());
		context.setPages(getPages());
		context.setSourceElement(getSourceElement());
		context.setType(getType());
		context.setStructuralFeature(getStructuralFeature());
		return context;
	}
}
