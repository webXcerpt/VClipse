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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.vclipse.vcml.vcml.Model;

/**
 * 
 */
public class VCML2IDocTransformation {
	
	
	/**
	 * @param iterator
	 * @param monitor
	 * @throws InvocationTargetException
	 */
	public void tranform(final Iterator<IFile> iterator, final IProgressMonitor monitor) throws InvocationTargetException {
		if(iterator != null) {
			monitor.beginTask("Convertng SAP models to IDocs", IProgressMonitor.UNKNOWN);
			final ResourceSet set = new XtextResourceSet();
			while(iterator.hasNext()) {
				if(monitor.isCanceled()) {
					break;
				} else {
					final IFile file = iterator.next();
					monitor.subTask("Converting " + file.getName());
					try {
						Resource resource = set.createResource(URI.createURI(file.getLocationURI().toString()));
						resource.load(null);
						final EList<EObject> contents = resource.getContents();
						if(!contents.isEmpty()) {
							final EObject eobject = contents.get(0);
							if(eobject instanceof Model) {
								final org.vclipse.idoc.iDoc.Model idocModel = new VCML2IDocSwitch().vcml2IDocs((Model)eobject);
								if(idocModel != null) {
									final IFile idocFile = file.getParent().getFile(new Path(file.getName()).removeFileExtension().addFileExtension("idoc"));
									if(!idocFile.exists()) {
										try {
											idocFile.create(new ByteArrayInputStream("".getBytes()), true, monitor);
											idocFile.setCharset("UTF-8", monitor);
										} catch(final CoreException exception) {
											VCML2IDocPlugin.log(exception.getMessage(), exception);
										}
									}
									resource = set.createResource(URI.createURI(idocFile.getLocationURI().toString()));
									resource.getContents().add(idocModel);
									resource.save(null);
								}
							}
						}
					} catch(final IOException exception) {
						throw new InvocationTargetException(exception);
					}
					monitor.internalWorked(1);
				}
			}
		}
	}
}
