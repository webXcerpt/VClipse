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
package org.vclipse.connection.wizards;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.vclipse.connection.internal.AbstractConnection;

/**
 *
 */
final class LabelProvider extends BaseLabelProvider implements ITableLabelProvider {
	
	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(final Object element, final int columnIndex) {
		if(element instanceof AbstractConnection) {
			switch (columnIndex) {
				case 1: return ((AbstractConnection)element).getSystemName();
				case 2: return ((AbstractConnection)element).getHostName();
				case 3: return ((AbstractConnection)element).getSystemNumber();
			}
		}
		return null;
	}
}
