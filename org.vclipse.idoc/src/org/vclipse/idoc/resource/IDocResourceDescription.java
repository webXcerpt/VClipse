/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.idoc.resource;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescription;

public class IDocResourceDescription extends DefaultResourceDescription {

	public IDocResourceDescription(final Resource resource,
			final IQualifiedNameProvider nameProvider) {
		super(resource, nameProvider);
	}

	@Override
	protected List<IEObjectDescription> computeExportedObjects() {
		return Collections.emptyList();
	}

}
