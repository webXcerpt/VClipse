/*******************************************************************************
 * Copyright (c) 2009 webXcerpt Software GmbH (http://www.webxcerpt.com).
 * All rights reserved.
 *******************************************************************************/
package org.vclipse.idoc2jcoidoc.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.views.IDocView;

/**
 * 
 */
public class ShowJCoIDocsAction implements IObjectActionDelegate {

	/**
	 * 
	 */
	private ISelection selection;
	
	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
		// nothing to do
	}
	
	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		this.selection = selection;
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(final IAction action) {
		if(selection instanceof IStructuredSelection) {
			try {
				final IDocView idocView = (IDocView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IDocView.ID);
				idocView.setInput((IFile)((IStructuredSelection)selection).getFirstElement());
			} catch (PartInitException e) {
				IDoc2JCoIDocPlugin.log(e.getMessage(), e);
			}
		}
	}
}
