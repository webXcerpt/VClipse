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
package org.vclipse.vcml2idoc.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.vclipse.vcml2idoc.VCML2IDocUIPlugin;
import org.vclipse.vcml2idoc.builder.VCML2IDocNature;

import com.google.common.collect.Lists;

public class AddRemoveNatureAction implements IActionDelegate {

	private IProject project;

	public void run(final IAction action) {
		ArrayList<String> natureIds;
		try {
			IProjectDescription description = project.getDescription();
			natureIds = Lists.newArrayList(description.getNatureIds());
			if(natureIds.contains(VCML2IDocNature.ID)) {
				natureIds.remove(VCML2IDocNature.ID);
			} else {
				natureIds.add(VCML2IDocNature.ID);
			}
			description.setNatureIds(natureIds.toArray(new String[natureIds.size()]));
			project.setDescription(description, new NullProgressMonitor());
		} catch (CoreException e) {
			VCML2IDocUIPlugin.log(e.getMessage(), e);
		}
	}

	public void selectionChanged(final IAction action, final ISelection selection) {
		if(selection instanceof IStructuredSelection) {
			if(!selection.isEmpty()) {
				Iterator<?> iterator = ((IStructuredSelection)selection).iterator();
				if(iterator.hasNext()) {
					Object next = iterator.next();
					if(next instanceof IProject) {
						project = (IProject)next;
					}
				}
			}
		}
	}
}
