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

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.vclipse.refactoring.ExtensionsReader;
import org.vclipse.refactoring.ui.IUIRefactoringContext;
import org.vclipse.refactoring.ui.UIRefactoringContext;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ModelChange extends Change {

	private static final String NAME_EXTENSION_CHANGED = "changed";
	private static final String NAME_EXTENSION_CURRENT = "current";

	@Inject
	private RefactoringRunner refactoring;
	
	@Inject
	private ExtensionsReader reader;
	
	private LanguageRefactoringProcessor processor;
	
	@Override
	public String getName() {
		EObject element = processor.getContext().getSourceElement();
		URI uri = element.eResource().getURI();
		return "Changes in the resource " + uri.lastSegment();
	}
	
	public void setProcessor(LanguageRefactoringProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void initializeValidationData(IProgressMonitor pm) {
		
	}

	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return null;
	}
	
	public EObject getCurrent() {
		IUIRefactoringContext context = processor.getContext();
		EObject sourceElement = context.getSourceElement();
		EObject containerCopy = rootContainerCopy(sourceElement, NAME_EXTENSION_CURRENT);
		return containerCopy;
	}
	
	public EObject getChanged() {
		IUIRefactoringContext context = processor.getContext();
		EObject sourceElement = context.getSourceElement();
		EObject copy = rootContainerCopy(sourceElement, NAME_EXTENSION_CHANGED);
		EObject equal = getEqualTo(sourceElement, copy);
		IUIRefactoringContext contextCopy = ((UIRefactoringContext)context).copy();
		if(equal != null) {
			contextCopy.setSourceElement(equal);
		}
		refactoring.refactor(contextCopy);		
		return copy;
	}

	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		final List<EObject> results = Lists.newArrayList();
		Runnable refactoringRunnable = new Runnable() {
			@Override
			public void run() {
				final IUIRefactoringContext context = processor.getContext();
				IXtextDocument document = context.getDocument();
				List<EObject> changes = document.modify(new IUnitOfWork<List<EObject>, XtextResource>() {
					@Override
					public List<EObject> exec(XtextResource state) throws Exception {
						List<EObject> results = refactoring.refactor(context);
						return results;
					}
				});
				results.addAll(changes);
			}
		};
		Display.getDefault().asyncExec(refactoringRunnable);
		EObject element = processor.getContext().getSourceElement();
		URI uri = element.eResource().getURI();
		return new CompositeChange("Changes in the resource " + uri.lastSegment(), new Change[]{this});
	}

	@Override
	public Object getModifiedElement() {
		return null;
	}
	
	public EObject rootContainerCopy(EObject object, String resourceNameExtension) {
		Resource resource = object.eResource();
		ResourceSet set = resource.getResourceSet();
		URI uri = URI.createURI("temporary_" + resourceNameExtension + "." + resource.getURI().fileExtension());
		try {
			resource = set.getResource(uri, true);
		} catch(Exception exception) {
			resource = set.getResource(uri, true);
		}
		resource.getContents().clear();
		EObject container = EcoreUtil.getRootContainer(object);
		EcoreUtil.Copier copier = new EcoreUtil.Copier(true);
		container = copier.copy(container);
		copier.copyReferences();
		resource.getContents().add(container);
		return container;
	}
	
	public static EObject getEqualTo(EObject searchFor, EObject rootContainer) {
		if(equals(searchFor, rootContainer)) {
			return rootContainer;
		}
		TreeIterator<EObject> contents = rootContainer.eAllContents();
		while(contents.hasNext()) {
			EObject next = contents.next();
			if(equals(searchFor, next)) {
				return next;
			}
		}
		return null;
	}
	
	public static boolean equals(EObject object_one, EObject object_two) {
		return EcoreUtil.equals(object_one, object_two) && EcoreUtil.equals(object_one.eContainer(), object_two.eContainer());
	}
}
