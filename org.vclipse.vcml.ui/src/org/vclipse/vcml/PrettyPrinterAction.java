/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
package org.vclipse.vcml;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.concurrent.IUnitOfWork;
import org.eclipse.xtext.parsetree.reconstr.SerializerUtil;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.core.editor.XtextEditor;
import org.vclipse.vcml.vcml.Model;

import com.google.inject.Inject;


/**
 * 
 */
public class PrettyPrinterAction implements IViewActionDelegate {
	
	/**
	 * 
	 */
	@Inject
	private SerializerUtil serializerUtil;
	
	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		
	}
 
	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(activeEditor instanceof XtextEditor) {
			XtextEditor editor = (XtextEditor)activeEditor;
			Model model = editor.getDocument().readOnly(new IUnitOfWork<Model, XtextResource>() {
				public Model exec(XtextResource state) throws Exception {
					return (Model)state.getParseResult().getRootASTElement();
				}
			});
			System.out.println(serializerUtil.serialize(model));
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		
	}
}
