/**
 * Copyright � 2008, 2010 webXcerpt Software GmbH.
 * All rights reserved.
 *  
 * Contributor :
 *               webXcerpt Software GmbH
 */
package org.vclipse.idoc2jcoidoc.views;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.vclipse.idoc2jcoidoc.Activator;
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
			return Activator.getImage(IUiConstants.IDOC_DOCUMENT_IMAGE);
		} else if (object instanceof IDocSegment) {
			return Activator.getImage(IUiConstants.IDOC_SEGMENT_IMAGE);
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
