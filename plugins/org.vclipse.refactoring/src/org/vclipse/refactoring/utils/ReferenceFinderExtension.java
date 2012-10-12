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
package org.vclipse.refactoring.utils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.ui.editor.findrefs.IReferenceFinder;
import org.eclipse.xtext.ui.editor.findrefs.SimpleLocalResourceAccess;
import org.eclipse.xtext.util.IAcceptor;
import org.vclipse.base.ui.util.EditorUtilsExtensions;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ReferenceFinderExtension {

	@Inject
	private IReferenceFinder finder;

	public List<IReferenceDescription> getReferences(EObject target, boolean allReferences) {
		final List<IReferenceDescription> references = Lists.newArrayList();
		Resource resource = target.eResource();
		if(resource != null) {
			IProgressMonitor pm = EditorUtilsExtensions.getProgressMonitor();
			pm = pm == null ? new NullProgressMonitor() : pm;
			ResourceSet resourceSet = resource.getResourceSet();
			SimpleLocalResourceAccess access = new SimpleLocalResourceAccess(resourceSet);
			IAcceptor<IReferenceDescription> acceptor = new IAcceptor<IReferenceDescription>() {
				public void accept(IReferenceDescription reference) {
					references.add(reference);
				}
			};
			Set<URI> resourceuris = Collections.singleton(resource.getURI());
			Set<URI> objecturis = Collections.singleton(EcoreUtil2.getNormalizedURI(target));
			pm.beginTask("Looking for references.", IProgressMonitor.UNKNOWN);
			if(allReferences) {
				finder.findAllReferences(objecturis, access, acceptor, new NullProgressMonitor());
			} else {
				finder.findReferences(objecturis, resourceuris, access, acceptor, new NullProgressMonitor());
			}			
			pm.done();
		}
		return references;
	}
}
