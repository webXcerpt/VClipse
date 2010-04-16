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
package org.vclipse.vcml2idoc.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.vclipse.vcml2idoc.VCML2IDocTransformation;

/**
 * 
 */
public class VCML2IDocsAction implements IObjectActionDelegate {

	/**
	 * 
	 */
	private Iterator<IFile> iterator;
	
	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
		// not used
	}
	
	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(final IAction action) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true, true, 
					new WorkspaceModifyOperation() {
						@Override
							protected void execute(final IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
								new VCML2IDocTransformation().tranform(iterator, monitor);
							}
					});
		} catch (final InvocationTargetException exception) {
			VCML2IDocUIPlugin.log(exception.getMessage(), exception);
		} catch (final InterruptedException exception) {
			VCML2IDocUIPlugin.log(exception.getMessage(), exception);
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection) {
			iterator = ((IStructuredSelection)selection).iterator();
		}
	}
}
