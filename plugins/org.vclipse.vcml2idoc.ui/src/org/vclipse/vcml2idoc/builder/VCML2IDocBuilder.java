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
package org.vclipse.vcml2idoc.builder;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.vclipse.vcml2idoc.VCML2IDocPlugin;
import org.vclipse.vcml2idoc.transformation.IVCML2IDocTransformation;

/**
 * 
 */
public class VCML2IDocBuilder extends IncrementalProjectBuilder {

	/**
	 * ID of the builder
	 */
	public static final String ID = "org.vclipse.builder.vcml2idoc";
	
	private static final String IDOC_EXTENSION = "idoc";

	private static final String VCML_EXTENSION = "vcml";
	
	private Set<IFile> pathsToBuild;
	
	
	private IVCML2IDocTransformation transformation;
	
	/**
	 * 
	 */
	public VCML2IDocBuilder() {
		pathsToBuild = new HashSet<IFile>();
		transformation = VCML2IDocPlugin.getDefault().getInjector().getInstance(IVCML2IDocTransformation.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected IProject[] build(final int kind, final Map args, final IProgressMonitor monitor) throws CoreException {
		final IProject project = getProject();
		pathsToBuild.clear();
		if(IncrementalProjectBuilder.FULL_BUILD == kind) {
			project.accept(new IResourceVisitor() {
				public boolean visit(final IResource resource) {
					collect(resource, VCML_EXTENSION);
					return false;
				}
			});
		} else {
			getDelta(project).accept(new IResourceDeltaVisitor() {
				public boolean visit(final IResourceDelta delta) {
					switch(delta.getKind()) {
						case IResourceDelta.ADDED : 
						case IResourceDelta.CHANGED :
							collect(delta.getResource(), VCML_EXTENSION);
							return true;
						default:
							return false;
					}
				}
			});
		}
		
		try {
			transformation.transform(pathsToBuild, monitor);
			monitor.done();
		} catch (final InvocationTargetException exception) {
			monitor.setCanceled(true);
			VCML2IDocPlugin.log(exception.getMessage(), exception);
		}
		return new IProject[0];
	}

	@Override
	protected void clean(final IProgressMonitor monitor) throws CoreException {
		final IProject project = getProject();
		pathsToBuild.clear();
		monitor.beginTask("Cleaning the project " + project.getName(), IProgressMonitor.UNKNOWN);
		project.accept(new IResourceVisitor() {
			@Override
			public boolean visit(final IResource resource) throws CoreException {
				collect(resource, IDOC_EXTENSION);
				return true;
			}
		});
		for(IFile file : pathsToBuild) {
			file.delete(IResource.FORCE | IResource.KEEP_HISTORY, monitor);
		}
		monitor.done();
	}

	private void collect(final IResource resource, final String extension) {
		if(IResource.FILE == resource.getType() && extension.equals(resource.getFileExtension())) {
			pathsToBuild.add((IFile)resource);
		}
	}
}
