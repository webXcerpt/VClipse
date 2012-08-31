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

import java.util.List;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.vclipse.refactoring.core.IRefactoringContext;

import com.google.inject.ImplementedBy;

@ImplementedBy(UIRefactoringContext.class)
public interface IUIRefactoringContext extends IRefactoringContext {

	public IXtextDocument getDocument();
	
	public void setDocument(IXtextDocument document);
	
	public void handleWidgets();
	
	public List<? extends UserInputWizardPage> getPages();
	
	public void setPages(List<? extends UserInputWizardPage> pages);
	
}
