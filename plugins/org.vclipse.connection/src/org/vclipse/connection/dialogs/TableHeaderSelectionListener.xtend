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
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Table
import org.eclipse.swt.widgets.TableColumn
import org.vclipse.connection.IConnectionHandler

/** 
 */
class TableHeaderSelectionListener extends SelectionAdapter {
	/** 
	 */
	final TableViewer tableViewer
	/** 
	 */
	final IConnectionHandler handler

	/** 
	 * @param tableViewer
	 */
	new(TableViewer tableViewer, IConnectionHandler connectionHandler) {
		this.tableViewer = tableViewer
		handler = connectionHandler
	}

	/** 
	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	override void widgetSelected(SelectionEvent event) {
		val TableColumn selectedColumn = event.widget as TableColumn
		val Table table = selectedColumn.getParent()
		var int sortDirection = table.getSortDirection()
		if (table.getSortColumn() === selectedColumn) {
			sortDirection = if(sortDirection === SWT::UP) SWT::DOWN else SWT::UP
		} else {
			table.setSortColumn(selectedColumn)
			sortDirection = SWT::UP
		}
		table.setSortDirection(sortDirection)
		tableViewer.getSorter().sort(tableViewer, handler.getAvailableConnections())
		tableViewer.refresh()
	}

}
