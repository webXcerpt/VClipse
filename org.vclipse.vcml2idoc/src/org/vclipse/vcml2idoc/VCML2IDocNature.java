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
package org.vclipse.vcml2idoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 */
public class VCML2IDocNature implements IProjectNature {

	/**
	 * 
	 */
	public static final String ID = "org.vclipse.natures.vcml2idoc";
	
	/**
	 * 
	 */
	private IProject project;
	
	/**
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	@Override
	public void configure() throws CoreException {
		if(project != null && project.isOpen()) {
			try {
				final IProjectDescription description = project.getDescription();
				final ICommand[] cmds = description.getBuildSpec();
				for (int j = 0; j < cmds.length; j++) {
					if (VCML2IDocBuilder.ID.equals(cmds[j].getBuilderName())) {
						return;
					}
				}

				// Associate builder with project.
				final ICommand newCmd = description.newCommand();
				newCmd.setBuilderName(VCML2IDocBuilder.ID);
				final List<ICommand> newCmds = new ArrayList<ICommand>();
				newCmds.addAll(Arrays.asList(cmds));
				newCmds.add(newCmd);
				description.setBuildSpec((ICommand[]) newCmds.toArray(new ICommand[newCmds.size()]));
				project.setDescription(description, null);
			} catch (final CoreException exception) {
				VCML2IDocPlugin.log(exception.getMessage(), exception);
			}
		}
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	@Override
	public void deconfigure() throws CoreException {
		if(project.isOpen()) {
			// Get the description.
			IProjectDescription description;
			try {
				description = project.getDescription();
				// Look for builder.
				int index = -1;
				final ICommand[] cmds = description.getBuildSpec();
				for (int j = 0; j < cmds.length; j++) {
					if (VCML2IDocBuilder.ID.equals(cmds[j].getBuilderName())) {
						index = j;
						break;
					}
				}
				if (index == -1) {
					return;
				}

				// Remove builder from project.
				final List<ICommand> newCmds = new ArrayList<ICommand>();
				newCmds.addAll(Arrays.asList(cmds));
				newCmds.remove(index);
				description.setBuildSpec((ICommand[]) newCmds.toArray(new ICommand[newCmds.size()]));
				project.setDescription(description, null);
			} catch (final CoreException exception) {
				VCML2IDocPlugin.log(exception.getMessage(), exception);
			}
		}
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	@Override
	public IProject getProject() {
		return project;
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	@Override
	public void setProject(final IProject project) {
		this.project = project;
	}

}
