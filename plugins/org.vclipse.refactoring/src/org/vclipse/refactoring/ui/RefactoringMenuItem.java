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

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
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
import org.vclipse.base.ui.BaseUiPlugin;
import org.vclipse.base.ui.util.EditorUtilsExtensions;
import org.vclipse.refactoring.ExtensionsReader;
import org.vclipse.refactoring.core.LanguageRefactoringProcessor;
import org.vclipse.refactoring.core.RefactoringCustomisation;
import org.vclipse.refactoring.core.RefactoringRunner;
import org.vclipse.refactoring.core.RefactoringType;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class RefactoringMenuItem extends ContributionItem implements SelectionListener {

	private static final String CONTEXT = "context";

	@Inject
	private Provider<IUIRefactoringContext> contextProvider;
	
	@Inject
	private RefactoringRunner refactoring;
	
	@Inject
	private EObjectAtOffsetHelper offsetHelper;
	
	@Inject
	private LanguageRefactoringProcessor refactoringProcessor;
	
	@Inject
	private ExtensionsReader extensionReader;
	
	@Inject
	private RefactoringUtility refactoringUtility;
	
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
					EObject rootContainer = EcoreUtil.getRootContainer(elementAt);
					Iterator<RefactoringCustomisation> iterator = extensionReader.getCustomisation().get(rootContainer.eClass()).iterator();
					if(iterator.hasNext()) {
						RefactoringCustomisation customisation = iterator.next();
						if(elementAt != null) {
							for(RefactoringType type : RefactoringType.values()) { 
								IUIRefactoringContext context = contextProvider.get();
								context.setSourceElement(elementAt);
								context.setType(type);
								for(EStructuralFeature feature : customisation.features(context)) {
									context = ((UIRefactoringContext)context).copy();
									context.setStructuralFeature(feature);
									if(customisation.evaluate(context)) {
										context.setDocument(editor.getDocument());
										createMenuItem(menu, context);
									}
								}
							}	
						}
					}	
				}
			}
		}
	}
	
	private void createMenuItem(Menu menu, IUIRefactoringContext context) {
		if(refactoring.isRefactoringAvailable(context)) {
			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(refactoringUtility.getRefactoringText(context));
			item.addSelectionListener(this);
			item.setData(CONTEXT, context);
		}
	}
	
	@Override
	public void widgetSelected(SelectionEvent event) {
		Object source = event.getSource();
		if(source instanceof MenuItem) {
			MenuItem item = (MenuItem)source;
			IUIRefactoringContext context = (IUIRefactoringContext)item.getData(CONTEXT);
			refactoringProcessor.setContext(context);
			try {
				Shell activeShell = Display.getDefault().getActiveShell();
				List<? extends UserInputWizardPage> pages = Lists.newArrayList();
				EObject sourceElement = context.getSourceElement();
				EObject rootContainer = EcoreUtil.getRootContainer(sourceElement);
				Iterator<RefactoringUICustomisation> iterator = extensionReader.getUICustomisation().get(rootContainer.eClass()).iterator();
				if(iterator.hasNext()) {
					pages = iterator.next().getPages(context);
				}
				new RefactoringWizardOpenOperation( 
						new LanguageRefactoringWizard(pages, 
								new ProcessorBasedRefactoring(refactoringProcessor), 
										RefactoringWizard.DIALOG_BASED_USER_INTERFACE)).run(activeShell, 
												refactoringUtility.getRefactoringText(context));
			} catch(InterruptedException exception) {
				BaseUiPlugin.log(exception.getMessage(), exception);
			}
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		
	}
}
