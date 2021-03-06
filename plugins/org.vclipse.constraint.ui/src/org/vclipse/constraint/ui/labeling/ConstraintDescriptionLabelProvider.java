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
package org.vclipse.constraint.ui.labeling;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.ui.label.DefaultDescriptionLabelProvider;

import com.google.inject.Inject;

/**
 * Provides labels for a IEObjectDescriptions and IResourceDescriptions.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class ConstraintDescriptionLabelProvider extends DefaultDescriptionLabelProvider {

	@Inject
	private ConstraintLabelProvider labelProvider;
	
	@Override
	public Object text(IReferenceDescription referenceDescription) {
		URI sourceUri = referenceDescription.getSourceEObjectUri();
		EObject object = new ResourceSetImpl().getEObject(sourceUri, true);
		return labelProvider.getText(object);
	}
	
	@Override
	public Object image(IReferenceDescription referenceDescription) {
		return labelProvider.getImage(referenceDescription);
	}
}
