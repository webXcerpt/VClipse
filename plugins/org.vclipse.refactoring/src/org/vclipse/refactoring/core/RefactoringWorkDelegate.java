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
package org.vclipse.refactoring.core;

import java.io.IOException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.SaveOptions;

public class RefactoringWorkDelegate {

	public void createResource(ResourceSet resourceSet, URI uri, EObject topLevelEntry, boolean save) throws IOException {
		Resource newConstraintResource = null;
		try {
			newConstraintResource = resourceSet.getResource(uri, true);
		} catch(Exception exception) {
			newConstraintResource = resourceSet.getResource(uri, true);
		}
		EList<EObject> contents = newConstraintResource.getContents();
		contents.add(topLevelEntry);
		if(save) {
			newConstraintResource.save(SaveOptions.defaultOptions().toOptionsMap());			
		}
	}
	
	public void saveResource(Resource resource) throws IOException {
		resource.save(SaveOptions.defaultOptions().toOptionsMap());
	}
}
