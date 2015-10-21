/** 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.idoc2jcoidoc.views

import org.eclipse.jface.viewers.BaseLabelProvider
import org.eclipse.jface.viewers.ILabelProvider
import org.eclipse.swt.graphics.Image
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin
import org.vclipse.idoc2jcoidoc.IUiConstants
import com.sap.conn.idoc.IDocDocument
import com.sap.conn.idoc.IDocSegment

/** 
 */
class IDocLabelProvider extends BaseLabelProvider implements ILabelProvider {
	/** 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	override Image getImage(Object object) {
		switch object {
		IDocDocument: IDoc2JCoIDocPlugin.getImage(IUiConstants.IDOC_DOCUMENT_IMAGE)
		IDocSegment:  IDoc2JCoIDocPlugin.getImage(IUiConstants.IDOC_SEGMENT_IMAGE)
		default:      null
		}
	}

	/** 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	override String getText(Object it) {
		switch it {
		IDocDocument: '''«IDocNumber» «IDocType» «messageType»'''
		IDocSegment:  '''«definition» («description»)'''
		default:      toString
		}
	}

}
