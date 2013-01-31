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
package org.vclipse.vcml.ui.outline;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.vclipse.vcml.vcml.VCObject;

@SuppressWarnings("rawtypes")
public class VCObjectAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject,  Class adapterType) {
		if(adaptableObject instanceof EObjectNode) {
			if(VCObject.class == adapterType) {
				EObject result = ((EObjectNode)adaptableObject).readOnly(new IUnitOfWork<EObject, EObject>() {
					public EObject exec(EObject object) throws Exception {
						return object;
					}
				});
				return result instanceof VCObject ? result : null;
			}
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { VCObject.class };
	}
}
