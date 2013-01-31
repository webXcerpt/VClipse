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
package org.vclipse.idoc2jcoidoc.views;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.IUiConstants;

import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocSegment;

/**
 *
 */
public class IDocLabelProvider extends BaseLabelProvider implements ILabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(final Object object) {
		if(object instanceof IDocDocument) {
			return IDoc2JCoIDocPlugin.getImage(IUiConstants.IDOC_DOCUMENT_IMAGE);
		} else if (object instanceof IDocSegment) {
			return IDoc2JCoIDocPlugin.getImage(IUiConstants.IDOC_SEGMENT_IMAGE);
		} else {
			return null;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(final Object object) {
		if(object instanceof IDocDocument) {
			final IDocDocument iDocDocument = (IDocDocument)object;
			return iDocDocument.getIDocNumber() + " " + iDocDocument.getIDocType() + " " + iDocDocument.getMessageType();
		} else if(object instanceof IDocSegment) {
			final IDocSegment iDocSegment = (IDocSegment)object;
			return iDocSegment.getDefinition() + " (" + iDocSegment.getDescription() + ")";
		} else {
			return object.toString();
		}
	}
}
