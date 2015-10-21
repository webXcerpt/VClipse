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

import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.Viewer
import org.eclipse.jface.viewers.ViewerSorter
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Table
import org.vclipse.connection.internal.AbstractConnection

/** 
 */
final class TableHeaderSorter extends ViewerSorter {
	/** 
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	override int compare(Viewer viewer, Object first, Object second) {
		val Table table = (viewer as TableViewer).getTable()
		val String text = table.getSortColumn().getText()
		var String first_str = ""
		var String second_str = ""
		if (text.equals("System name")) {
			first_str = (first as AbstractConnection).getSystemName()
			second_str = (second as AbstractConnection).getSystemName()
		} else if (text.equals("Host name")) {
			first_str = (first as AbstractConnection).getHostName()
			second_str = (second as AbstractConnection).getHostName()
		} else if (text.equals("User name")) {
			first_str = (first as AbstractConnection).getUserName()
			second_str = (second as AbstractConnection).getUserName()
		} else if (text.equals("System number")) {
			first_str = (first as AbstractConnection).getSystemNumber()
			second_str = (second as AbstractConnection).getSystemNumber()
		} else if (text.equals("Client number")) {
			first_str = (first as AbstractConnection).getClientNumber()
			second_str = (second as AbstractConnection).getClientNumber()
		}
		if (SWT::UP === table.getSortDirection()) {
			return first_str.compareTo(second_str)
		} else {
			return -first_str.compareTo(second_str)
		}
	}

}
