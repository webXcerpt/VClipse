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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml2idoc.IVcml2IDocTransformation;
import org.vclipse.vcml2idoc.VCML2IDocUIPlugin;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

/**
 *
 */
public class VCML2IDocTransformation implements IVcml2IDocTransformation {

	@Inject
	private VCML2IDocSwitch vcml2IDocSwitch;

	@Override
	public void tranform(final Iterator<IFile> iterator, final IProgressMonitor monitor) throws InvocationTargetException {
		if(iterator != null) {
			monitor.beginTask("Converting VCML to IDoc:", IProgressMonitor.UNKNOWN);
			final ResourceSet set = new XtextResourceSet();
			while(iterator.hasNext()) {
				if(monitor.isCanceled()) {
					break;
				} else {
					final IFile file = iterator.next();
					monitor.subTask("Reading " + file.getName());
					try {
						Resource resource = set.createResource(URI.createURI(file.getLocationURI().toString()));
						Map<Object,Object> options = Maps.newHashMap();
						options.put(XtextResource.OPTION_ENCODING, "UTF-8");
						resource.load(options);
						final EList<EObject> contents = resource.getContents();
						if(!contents.isEmpty()) {
							final EObject eobject = contents.get(0);
							if(eobject instanceof Model) {
								monitor.subTask("Converting " + file.getName());
								final org.vclipse.idoc.iDoc.Model idocModel = vcml2IDocSwitch.vcml2IDocs((Model)eobject);
								if(idocModel != null) {
									monitor.subTask("Serializing " + file.getName());
									final IFile idocFile = file.getParent().getFile(new Path(file.getName()).removeFileExtension().addFileExtension("idoc"));
									if(!idocFile.exists()) {
										try {
											idocFile.create(new ByteArrayInputStream("".getBytes()), true, monitor);
											idocFile.setCharset("UTF-8", monitor);
										} catch(final CoreException exception) {
											VCML2IDocUIPlugin.log(exception.getMessage(), exception);
										}
									}
									resource = set.createResource(URI.createURI(idocFile.getLocationURI().toString()));
									resource.getContents().add(idocModel);
									resource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
									try {
										idocFile.refreshLocal(IResource.DEPTH_ONE, monitor);
									} catch (CoreException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
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
