package org.vclipse.base;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

public class UriUtil {

	public static final String SEPARATOR = "/";
	
	public static final String STEP_UP = "../";
	
	public static final String EMPTY = "";

	public String computeImportUri(Resource importResource, Resource targetResource) {
		URI importResourceUri = importResource.getURI();
		URI targetResourceUri = targetResource.getURI();
		if(!importResourceUri.scheme().equals(targetResourceUri.scheme()) ||
				!importResourceUri.device().equals(targetResourceUri.device())) {
			return EMPTY;
		} else {
			StringBuffer importUri = new StringBuffer();
			String[] importUriParts = importResourceUri.toString().split(SEPARATOR);
			String[] targetUriParts = targetResourceUri.toString().split(SEPARATOR);
			
			if(importUriParts.length == targetUriParts.length) {
				return importUriParts[importUriParts.length - 1];
			} else if(importUriParts.length < targetUriParts.length) {
				for(int i=0; i<targetUriParts.length; i++) {
					if(i < importUriParts.length) {
						if(importUriParts[i].equals(targetUriParts[i])) {
							continue;
						} else {
							importUri.append(STEP_UP);
						}
					} else {
						if(i == targetUriParts.length - 1) {
							importUri.append(importUriParts[importUriParts.length - 1]);
						} else {
							importUri.append(STEP_UP);
						}
					}
				}
			} else {
				for(int index=0; index<importUriParts.length; index++) {
					if(index < targetUriParts.length) {
						if(importUriParts[index].equals(targetUriParts[index])) {
							continue;
						} else if(!importUriParts[index].equals(targetUriParts[index])) {
							importUri.append(importUriParts[index]);
						}
					} else {
						if(index < importUriParts.length) {
							importUri.append(SEPARATOR);
						}
						importUri.append(importUriParts[index]);
					}
				}
			}
			return importUri.toString();
		}
	}
}
