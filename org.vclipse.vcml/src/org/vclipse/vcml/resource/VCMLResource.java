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
package org.vclipse.vcml.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IFragmentProvider;
import org.eclipse.xtext.resource.XtextResource;

public class VCMLResource extends XtextResource {

	// don't use super methods
	@Override
	public EObject getEObject(String uriFragment) {
		IFragmentProvider fragmentProvider = getFragmentProvider();
		EObject result = (fragmentProvider != null) ? fragmentProvider.getEObject(this, uriFragment) : null;

		return result;
	}

	@Override
	public String getURIFragment(EObject object) {
		IFragmentProvider fragmentProvider = getFragmentProvider();
		String result = (fragmentProvider != null) ? fragmentProvider.getFragment(object) : null;

		return result;
	}

}
