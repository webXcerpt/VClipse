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
package org.vclipse.connection.dialogs;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.ISharedImages;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.connection.internal.AbstractConnection;

import com.google.inject.Inject;

/**
 *
 */
public class LabelProvider extends BaseLabelProvider implements ITableLabelProvider, ITableColorProvider {
	
	/**
	 * 
	 */
	private final IConnectionHandler handler;
	
	/**
	 * @param handler
	 */
	@Inject
	public LabelProvider(IConnectionHandler connectionHandler) {
		handler = connectionHandler;
	}
	
	/**
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(final Object element, final int columnIndex) {
		if(element instanceof AbstractConnection && columnIndex == 0) {
			if(((AbstractConnection)element).equals(handler.getCurrentConnection())) {
				return VClipseConnectionPlugin.getImage(ISharedImages.CONNECTED_IMAGE);
			} else {
				return VClipseConnectionPlugin.getImage(ISharedImages.DISCONNECTED_IMAGE);
			}
		}	
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
				case 3: return ((AbstractConnection)element).getUserName();
				case 4: return ((AbstractConnection)element).getSystemNumber();
				case 5: return ((AbstractConnection)element).getClientNumber();
			}
		}
		return null;
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		/*
		if(element instanceof Connection) {
			if(((Connection)element).equals(VClipseConnectionPlugin.getDefault().getConnectionHandler().getCurrentConnection())) {
				return backgroundColor == null ? backgroundColor = new Color(Display.getCurrent(), 255, 181, 21) : backgroundColor;
			}
		}	*/
		return null;
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		return null;
	}
}
