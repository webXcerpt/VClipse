/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.ui.refactoring;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.vclipse.vcml.ui.VCMLUiPlugin;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 *	Extension of the default "Resource re-name re-factoring" for vc objects of type Dependency.
 *
 *	Since these objects, living in a vcml file, could have a reference(file) containing dependency code, the rename
 *	refactoring on them should also rename the referenced files. 
 *
 *	The rename refactoring on the dependency files should also rename the dependency objects.
 */
public class VcmlRenameDependencyParticipant extends RenameParticipant {

	@Inject
	private DependencySourceUtils dependencySourceUtils;
	
	@Override
	protected boolean initialize(Object object) {
		if(object instanceof IFile) {
			IFile file = (IFile)object;
			String extension = file.getFileExtension();
			return getDependencyExtensions().contains(extension);
		}
		return false;
	}

	@Override
	public String getName() {
		return VcmlRenameDependencyParticipant.class.getSimpleName();
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor progressMonitor, CheckConditionsContext context) throws OperationCanceledException {
		// TODO ? need any code here
//		ResourceChangeChecker resourceChecker = (ResourceChangeChecker)context.getChecker(ResourceChangeChecker.class);
//		if(resourceChecker != null) {
//			IResourceChangeDescriptionFactory deltaFactory = resourceChecker.getDeltaFactory();
//			IResource resource = deltaFactory.getDelta().getResource();
//			deltaFactory.delete(resource);
//		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}

	@Override
	public Change createChange(IProgressMonitor progressMonitor) throws CoreException, OperationCanceledException {
		Object[] elements = getProcessor().getElements();
		if(elements.length > 0 && elements[0] instanceof IFile) {
			IFile file = (IFile)elements[0];
			String path = file.getFullPath().toString();
			URI uri = URI.createPlatformResourceURI(path, true);
			Resource dependencyResource = new XtextResourceSet().getResource(uri, true);
			VCObject dependency = dependencySourceUtils.getDependency(dependencyResource.getURI());
			if(dependency != null) {
				dependency.eSet(VcmlPackage.eINSTANCE.getVCObject_Name(), getArguments().getNewName().replace("." + file.getFileExtension(), ""));
				try {
					dependency.eResource().save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
				} catch(IOException exception) {
					VCMLUiPlugin.log(exception.getMessage(), exception);
				}				
			}
		}
		return super.createPreChange(progressMonitor);
	}
	
	/**
	 * Returns a set with file extensions for this refactoring.
	 */
	protected Set<String> getDependencyExtensions() {
		return Sets.newHashSet(
				DependencySourceUtils.EXTENSION_CONSTRAINT, 
				DependencySourceUtils.EXTENSION_PRECONDITION, 
				DependencySourceUtils.EXTENSION_PROCEDURE,
				DependencySourceUtils.EXTENSION_SELECTIONCONDITION
		);
	}
}
