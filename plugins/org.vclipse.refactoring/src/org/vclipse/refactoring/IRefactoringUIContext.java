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

import java.util.List;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.vclipse.refactoring.core.RefactoringTask;
import org.vclipse.refactoring.ui.UIRefactoringContext;

import com.google.inject.ImplementedBy;

@ImplementedBy(UIRefactoringContext.class)
public interface IRefactoringUIContext extends IRefactoringContext {

	public void setRefactoring(RefactoringTask refactoring);
	
	public void setDocument(IXtextDocument document);
	
	public void configureWidgets();
	
	public void setPages(List<? extends UserInputWizardPage> pages);
	
	public IXtextDocument getDocument();
	
	public List<? extends UserInputWizardPage> getPages();
	
	public RefactoringTask getRefactoring();
	
}
