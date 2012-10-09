package org.vclipse.base.ui.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.util.ResourceUtil;

public class VClipseResourceUtil extends ResourceUtil {

	private ResourceSet defaultResourceSet;
	
	public ResourceSet getResourceSet() {
		if(defaultResourceSet == null) {
			defaultResourceSet = new XtextResourceSet();
		}
		return defaultResourceSet;
	}
	
	public Resource getResource(ResourceSet resourceSet, IFile file) {
		if(resourceSet == null) {
			if(defaultResourceSet == null) {
				defaultResourceSet = new XtextResourceSet();
			}
			resourceSet = defaultResourceSet;
		}
		String path = file.getFullPath().toString();
		URI uri = URI.createPlatformResourceURI(path, true);
		try {
			return resourceSet.getResource(uri, true);							
		} catch(Exception exception) {
			// resource does not exist
			return resourceSet.createResource(uri);				
		}
	}
}
