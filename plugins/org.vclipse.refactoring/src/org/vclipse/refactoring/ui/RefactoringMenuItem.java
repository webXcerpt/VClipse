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
package org.vclipse.refactoring.ui;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.vclipse.base.ui.util.EditorUtilsExtensions;
import org.vclipse.refactoring.IRefactoringConfiguration;
import org.vclipse.refactoring.IRefactoringUIConfiguration;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.core.RefactoringRunner;
import org.vclipse.refactoring.core.RefactoringTask;
import org.vclipse.refactoring.core.RefactoringType;
import org.vclipse.refactoring.utils.Extensions;
import org.vclipse.refactoring.utils.Labels;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class RefactoringMenuItem extends ContributionItem implements SelectionListener {

	private static final String CONTEXT = "context";

	@Inject
	private Provider<IRefactoringUIContext> contextProvider;
	
	@Inject
	private RefactoringTask refactoringTask;
	
	@Inject
	private RefactoringRunner refactoringRunner;
	
	@Inject
	private EObjectAtOffsetHelper offsetHelper;
	
	@Inject
	private Extensions extensions;
	
	@Inject
	private Labels labels;
	
	public RefactoringMenuItem() {
		super("com.webxcerpt.cm.nsn.cml.ui.refactoring.menuCreator");
	}

	@Override
	public void fill(Menu menu, int index) {
		// fill the menu
		XtextEditor editor = EditorUtils.getActiveXtextEditor();
		if(editor != null) {
			EObject object = EditorUtilsExtensions.getRootElement(editor);
			if(object != null) {
				ISelection selection = editor.getSelectionProvider().getSelection();
				if(selection instanceof ITextSelection) {
					ITextSelection textSelection = (ITextSelection)selection;
					XtextResource xtextResource = (XtextResource)object.eResource();
					EObject elementAt = offsetHelper.resolveContainedElementAt(xtextResource, textSelection.getOffset());
					EObject container = elementAt.eContainer();
					EObject rootContainer = EcoreUtil.getRootContainer(elementAt);
					IRefactoringConfiguration configuration = extensions.getInstance(IRefactoringConfiguration.class, rootContainer);
					if(elementAt != null && configuration != null) {
						for(RefactoringType type : RefactoringType.values()) { 
							IRefactoringUIContext context = contextProvider.get();
							context.setSourceElement(elementAt);
							context.setType(type);
							List<? extends EStructuralFeature> interestedInFeatures = configuration.provideFeatures(context);
							for(EStructuralFeature interestingFeature : interestedInFeatures) {
								context = ((UIRefactoringContext)context).copy();
								context.setStructuralFeature(interestingFeature);
								if(configuration.initialize(context)) {
									context.setDocument(editor.getDocument());
									if(refactoringRunner.isRefactoringAvailable(context)) {
										MenuItem item = new MenuItem(menu, SWT.PUSH);
										
										EReference containment = elementAt.eContainmentFeature();
										Object value = container.eGet(containment);
										if(value instanceof List<?>) {
											List<?> entries = (List<?>)value;
											int indexOf = entries.indexOf(elementAt);
											context.setIndex(indexOf);
										}
										
										String refactoringText = context.getLabel();
										item.setText(refactoringText);
										item.addSelectionListener(this);
										item.setData(CONTEXT, context);
									}
								}
							}
						}	
					}
				}
			}
		}
	}
	
	@Override
	public void widgetSelected(SelectionEvent event) {
		Object source = event.getSource();
		if(source instanceof MenuItem) {
			MenuItem item = (MenuItem)source;
			IRefactoringUIContext context = (IRefactoringUIContext)item.getData(CONTEXT);
			try {
				EObject sourceElement = context.getSourceElement();
				EObject rootContainer = EcoreUtil.getRootContainer(sourceElement);
				List<? extends UserInputWizardPage> pages = Lists.newArrayList();
				try {
					IRefactoringUIConfiguration uiConfiguration = extensions.getInstance(IRefactoringUIConfiguration.class, rootContainer);
					pages = uiConfiguration.provideWizardPages(context);
				} catch(Exception exception) {
					
				}
				refactoringTask.setContext(context);
				context.setRefactoring(refactoringTask);
				RefactoringWizard wizard = new RefactoringWizard(pages, refactoringTask, RefactoringWizard.DIALOG_BASED_USER_INTERFACE);
				Shell activeShell = Display.getDefault().getActiveShell();
				RefactoringWizardOpenOperation operation = new RefactoringWizardOpenOperation(wizard);
				operation.run(activeShell, labels.getUILabel(context));
			} catch(InterruptedException exception) {
				RefactoringPlugin.log(IStatus.ERROR, exception.getMessage());
			}
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		
	}
}
