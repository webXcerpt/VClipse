/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator and others
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan.vcmlt.naming;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.vclipse.base.naming.INameProvider;

import com.google.inject.Inject;

public class SapNameProvider extends INameProvider.AbstractImpl {

	@Inject
	IQualifiedNameProvider qualifiedNameProvider;
	
	@Override
	public String getName(final EObject obj) {
		return qualifiedNameProvider.getFullyQualifiedName(obj).getLastSegment();
	}

}
