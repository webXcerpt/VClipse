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
package org.vclipse.vcml2idoc.transformation;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.inject.ImplementedBy;

@ImplementedBy(VCML2IDocTransformation.class)
public interface IVCML2IDocTransformation {

	public void transform(Resource vcmlResource, Resource idocResource, IProgressMonitor monitor) throws InvocationTargetException;
	
	public void transform(IFile vcmlFile, IProgressMonitor monitor) throws InvocationTargetException;
	
	public void transform(Iterable<IFile> fileCollection, IProgressMonitor monitor) throws InvocationTargetException;
	
}
