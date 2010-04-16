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

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.vclipse.vcml2idoc.VCML2IDocNature;



/**
 * 
 */
public class AddRemoveVCML2IDocNature implements IActionDelegate {

	/**
	 * 
	 */
	private Iterator<IProject> projectIterator;

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(final IAction action) {
		while(projectIterator.hasNext()) {
			final IProject project = projectIterator.next();
			try {
				final IProjectDescription description = project.getDescription();
				final String[] natureids = description.getNatureIds();
				if(description.hasNature(VCML2IDocNature.ID)) {
					for(int i=0; i<natureids.length; i++) {
						if(natureids[i].equals(VCML2IDocNature.ID)) {
							String[] newids = null;
							if(i == 0) {
								newids = Arrays.copyOfRange(natureids, 0, natureids.length - 1);
							} else if(i == natureids.length -1) {
								newids = Arrays.copyOfRange(natureids, 0, natureids.length - 2);
							} else {
								newids = new String[natureids.length - 1];
								for(int k=0; k<natureids.length; i++) {
									if(i == k) {
										k--;
										continue;
									}
									newids[k] = natureids[i];
								}
							}
							description.setNatureIds(newids);
							project.setDescription(description, new NullProgressMonitor());
							break;
						}
					}
				} else {
					final String[] newNatureIds = Arrays.copyOf(natureids, natureids.length + 1);
					newNatureIds[natureids.length] = VCML2IDocNature.ID;
					description.setNatureIds(newNatureIds);
					project.setDescription(description, new NullProgressMonitor());
				}
			} catch (final CoreException exception) {
				VCML2IDocUIPlugin.log(exception.getMessage(), exception);
			}
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@SuppressWarnings("unchecked")
	public void selectionChanged(final IAction action, final ISelection selection) {
		if(selection instanceof IStructuredSelection && !selection.isEmpty()) {
			projectIterator = ((IStructuredSelection)selection).iterator();
		}
	}
}
