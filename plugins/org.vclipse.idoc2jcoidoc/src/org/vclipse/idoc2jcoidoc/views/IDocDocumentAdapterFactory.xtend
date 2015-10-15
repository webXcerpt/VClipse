/** 
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.idoc2jcoidoc.views

import org.eclipse.core.runtime.IAdapterFactory
import org.eclipse.ui.views.properties.IPropertySource
import com.sap.conn.idoc.IDocRecord

/** 
 */
class IDocDocumentAdapterFactory implements IAdapterFactory {
	/** 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	@SuppressWarnings("rawtypes") override Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType === typeof(IPropertySource) && adaptableObject instanceof IDocRecord) {
			return new IDocDocumentPropertySource(adaptableObject as IDocRecord)
		}
		return null
	}

	/** 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@SuppressWarnings("rawtypes") override Class[] getAdapterList() {
		return ( #[typeof(IPropertySource)] as Class[])
	}

}
