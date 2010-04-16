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
package org.vclipse.vcml.linking;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.linking.lazy.LazyLinker;

public class VCMLLinker extends LazyLinker {

	@Override
	protected void clearAllReferences(EObject model) {
		// this is intentional, the VCMLLinker should not clear all references
	}
	
}
