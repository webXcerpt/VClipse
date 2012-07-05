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
package org.vclipse.vcml2idoc.transformation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.resource.SaveOptions;
import org.vclipse.idoc.iDoc.IDocFactory;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml2idoc.VCML2IDocPlugin;

import com.google.inject.Inject;

public class VCML2IDocTransformation implements IVCML2IDocTransformation {

	private static final String IDOC_EXTENSION = "idoc";
	private static final String VCML_EXTENSION = "vcml";

	private static final Logger LOGGER = Logger.getLogger(VCML2IDocTransformation.class);
	
	@Inject
	private VCML2IDocSwitch vcml2IDocSwitch;
	
	public void transform(Resource vcmlResource, Resource idocResource, IProgressMonitor monitor) throws InvocationTargetException {
		EList<EObject> contents = vcmlResource.getContents();
		if(!contents.isEmpty()) {
			EObject object = contents.get(0);
			if(object instanceof Model) {
				monitor.subTask("Converting " + vcmlResource.getURI().lastSegment());
				org.vclipse.idoc.iDoc.Model idocModel = vcml2IDocSwitch.vcml2IDocs((Model)object);
				if(idocModel == null) {
					idocModel = IDocFactory.eINSTANCE.createModel();
				}
				idocResource.getContents().add(idocModel);
				try {
					idocResource.save(SaveOptions.defaultOptions().toOptionsMap());
				} catch (IOException exception) {
					VCML2IDocPlugin.log(exception.getMessage(), exception);
				}
			}
		}
	}
	
	public void transform(IFile vcmlFile, IProgressMonitor monitor) throws InvocationTargetException {
		if(vcmlFile.getFileExtension().equals(VCML_EXTENSION)) {
			URI vcmlUri = URI.createURI(vcmlFile.getFullPath().toString());
			URI idocUri = vcmlUri.trimFileExtension().appendFileExtension(IDOC_EXTENSION);
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource vcmlResource = resourceSet.getResource(vcmlUri, true);
			Resource idocResource;
			try {
				idocResource = resourceSet.getResource(idocUri, true);
			} catch(Exception exception) {
				idocResource = resourceSet.createResource(idocUri, "UTF-8");
			} 
			transform(vcmlResource, idocResource, monitor);
		} else {
			LOGGER.error("Only vcml files can be transformed. Got " + vcmlFile.getName());
		}
	}
	
	public void transform(Iterable<IFile> fileCollection, IProgressMonitor monitor) throws InvocationTargetException {
		monitor.beginTask("Converting vcml files to idoc files: ", IProgressMonitor.UNKNOWN);
		Iterator<?> iterator = fileCollection.iterator();
		while(iterator.hasNext()) {
			if(monitor.isCanceled()) {
				monitor.done();
				return;
			}
			Object next = iterator.next();
			if(next instanceof IFile) {
				IFile file = (IFile)next;
				monitor.subTask("Running transformation for file " + file.getName());
				transform(file, monitor);
			}
		}
	}
}
