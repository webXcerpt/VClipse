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
package org.vclipse.vcml.serializer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parsetree.reconstr.XtextSerializationException;
import org.eclipse.xtext.parsetree.reconstr.impl.CrossReferenceSerializer;

public class VCMLCrossReferenceSerializer extends CrossReferenceSerializer {
	
	@Override
	public String serializeCrossRef(EObject context, CrossReference grammarElement, EObject target, INode node) {
		try {
			// System.err.println("serializeCrossRef " + context + " " + grammarElement + " " + target);
			return super.serializeCrossRef(context, grammarElement, target, node);
		} catch (XtextSerializationException e) {
			if (target.eIsProxy()) {
				return ((InternalEObject)target).eProxyURI().fragment();
			} else {
				throw e;
			}
		}
	}

}
