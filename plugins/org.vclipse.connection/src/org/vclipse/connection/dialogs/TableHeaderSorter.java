/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
/***  ****//**
 * 
 */
package org.vclipse.connection.dialogs;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.vclipse.connection.internal.AbstractConnection;

/**
 *	
 */
public final class TableHeaderSorter extends ViewerSorter {

	/**
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object first, Object second) {
		final Table table = ((TableViewer)viewer).getTable();
		final String text = table.getSortColumn().getText();
		String first_str = "", second_str = "";
		if(text.equals("System name")) {
			first_str = ((AbstractConnection)first).getSystemName();
			second_str = ((AbstractConnection)second).getSystemName();
		} else if(text.equals("Host name")) {
			first_str = ((AbstractConnection)first).getHostName();
			second_str = ((AbstractConnection)second).getHostName();
		} else if(text.equals("User name")) {
			first_str = ((AbstractConnection)first).getUserName();
			second_str = ((AbstractConnection)second).getUserName();
		} else if(text.equals("System number")) {
			first_str = ((AbstractConnection)first).getSystemNumber();
			second_str = ((AbstractConnection)second).getSystemNumber();
		} else if(text.equals("Client number")) {
			first_str = ((AbstractConnection)first).getClientNumber();
			second_str = ((AbstractConnection)second).getClientNumber();
		}
		if(SWT.UP == table.getSortDirection()) {
			return first_str.compareTo(second_str);
		} else {
			return -first_str.compareTo(second_str);
		}
	}
}
