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
package org.vclipse.connection.dialogs

import org.eclipse.jface.viewers.BaseLabelProvider
import org.eclipse.jface.viewers.ITableColorProvider
import org.eclipse.jface.viewers.ITableLabelProvider
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Image
import org.vclipse.connection.IConnectionHandler
import org.vclipse.connection.ISharedImages
import org.vclipse.connection.VClipseConnectionPlugin
import org.vclipse.connection.internal.AbstractConnection
import com.google.inject.Inject

/** 
 */
class LabelProvider extends BaseLabelProvider implements ITableLabelProvider, ITableColorProvider {
	/** 
	 */
	final IConnectionHandler handler

	/** 
	 * @param handler
	 */
	@Inject new(IConnectionHandler connectionHandler) {
		handler = connectionHandler
	}

	/** 
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
	 */
	override void dispose() {
		super.dispose()
	}

	/** 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	override Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof AbstractConnection && columnIndex === 0) {
			if ((element as AbstractConnection).equals(handler.getCurrentConnection())) {
				return VClipseConnectionPlugin::getImage(ISharedImages::CONNECTED_IMAGE)
			} else {
				return VClipseConnectionPlugin::getImage(ISharedImages::DISCONNECTED_IMAGE)
			}
		}
		return null
	}

	/** 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	override String getColumnText(Object element, int columnIndex) {
		if (element instanceof AbstractConnection) {

			switch (columnIndex) {
				case 1: {
					return (element as AbstractConnection).getSystemName()
				}
				case 2: {
					return (element as AbstractConnection).getHostName()
				}
				case 3: {
					return (element as AbstractConnection).getUserName()
				}
				case 4: {
					return (element as AbstractConnection).getSystemNumber()
				}
				case 5: {
					return (element as AbstractConnection).getClientNumber()
				}
			}
		}
		return null
	}

	override Color getBackground(Object element, int columnIndex) {
		/*
		 * if(element instanceof Connection) {
		 * 	if(((Connection)element).equals(VClipseConnectionPlugin.getDefault().getConnectionHandler().getCurrentConnection())) {
		 * 		return backgroundColor == null ? backgroundColor = new Color(Display.getCurrent(), 255, 181, 21) : backgroundColor;
		 * 	}
		 }	*/
		return null
	}

	override Color getForeground(Object element, int columnIndex) {
		return null
	}

}
