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
