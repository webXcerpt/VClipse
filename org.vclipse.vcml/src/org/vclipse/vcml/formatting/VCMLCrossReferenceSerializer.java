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
package org.vclipse.vcml.formatting;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.parsetree.reconstr.IInstanceDescription;
import org.eclipse.xtext.parsetree.reconstr.XtextSerializationException;
import org.eclipse.xtext.parsetree.reconstr.impl.DefaultCrossReferenceSerializer;

public class VCMLCrossReferenceSerializer extends
		DefaultCrossReferenceSerializer {
	
	@Override
	public String serializeCrossRef(IInstanceDescription container,
			CrossReference grammarElement, EObject target) {
		try {
			System.err.println("serializeCrossRef " + container + " " + grammarElement + " " + target);
			return super.serializeCrossRef(container, grammarElement, target);
		} catch (XtextSerializationException e) {
			if (target.eIsProxy()) {
				return ((InternalEObject)target).eProxyURI().fragment();
			} else {
				throw e;
			}
		}
	}

}
