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
package org.vclipse.refactoring.changes;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.change.ChangeFactory;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.xtext.resource.SaveOptions;
import org.vclipse.refactoring.RefactoringPlugin;

public class ResourceChange extends ModelChangeEntry {

	private final Resource resource;
	
	public ResourceChange(Resource resource) {
		this.resource = resource;
		EObject refactored = resource.getContents().get(0);
		FeatureChange featureChange = ChangeFactory.eINSTANCE.createFeatureChange();
		addChange(null, refactored, featureChange);
	}
	
	@Override
	public Object getModifiedElement() {
		return resource;
	}

	@Override
	public String getName() {
		return "Re-factoring on resource " + resource.getURI().lastSegment();
	}
	
	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		if(isEnabled()) {
			try {
				resource.save(SaveOptions.defaultOptions().toOptionsMap());
			} catch (IOException exception) {
				IStatus status = new Status(IStatus.ERROR, RefactoringPlugin.ID, exception.getMessage());
				throw new CoreException(status);
			}
		}
		return null;
	}
}
