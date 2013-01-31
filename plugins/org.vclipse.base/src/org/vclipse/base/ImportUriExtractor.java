/*******************************************************************************
 * Copyright (c) 2008 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.base;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 *
 */
public class ImportUriExtractor {
	
	public static final String SEPARATOR = "/";
	public static final String STEP_UP = "../";
	public static final String EMPTY = "";

	/**
	 * 
	 */
	public String getImportUri(Resource importResource, Resource targetResource) {
		URI importResourceUri = importResource.getURI();
		URI targetResourceUri = targetResource.getURI();
		
		if(!importResourceUri.scheme().equals(targetResourceUri.scheme())) {
			return EMPTY;
		} 
		
		String targetDevice = targetResourceUri.device();
		String importDevice = importResourceUri.device();
		
		if(importDevice != null && targetDevice != null && !importDevice.equals(targetDevice)) {
			return EMPTY;
		}
		
		return getImportUri(importResourceUri.toString(), targetResourceUri.toString());
	}

	/**
	 * 
	 */
	protected String getImportUri(String importString, String targetString) {
		StringBuffer importUri = new StringBuffer();
		String[] importUriParts = importString.toString().split(SEPARATOR);
		String[] targetUriParts = targetString.toString().split(SEPARATOR);
		
		if(importUriParts.length == targetUriParts.length) {
			for(int i=0; i<targetUriParts.length; i++) {
				if(importUriParts[i].equals(targetUriParts[i])) {
					continue;
				} 
				if(i == targetUriParts.length - 1) {
					importUri.append(importUriParts[importUriParts.length - 1]);
					continue;
				} 
				importUri.append(STEP_UP);
				importUri.append(importUriParts[i]);
				importUri.append(SEPARATOR);
			}
			return importUri.toString();
		} 
		if(importUriParts.length < targetUriParts.length) {
			for(int i=0; i<targetUriParts.length; i++) {
				if(i < importUriParts.length) {
					if(importUriParts[i].equals(targetUriParts[i])) {
						continue;
					}
					importUri.append(STEP_UP);
				} else {
					if(i == targetUriParts.length - 1) {
						importUri.append(importUriParts[importUriParts.length - 1]);
					} else {
						importUri.append(STEP_UP);
					}
				}
			}
			return importUri.toString();
		} 
		for(int index=0; index<importUriParts.length; index++) {
			if(index < targetUriParts.length) {
				if(importUriParts[index].equals(targetUriParts[index])) {
					continue;
				}
				if(!importUriParts[index].equals(targetUriParts[index])) {
					importUri.append(importUriParts[index]);
				}
			} else {
				if(index < importUriParts.length) {
					importUri.append(SEPARATOR);
				}
				importUri.append(importUriParts[index]);
			}
		}
		return importUri.toString();
	}
}
