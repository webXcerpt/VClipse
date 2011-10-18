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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.vclipse.connection.IConnectionHandler;

/**
 *
 */
public class TableHeaderSelectionListener extends SelectionAdapter {

	/**
	 * 
	 */
	private final TableViewer tableViewer;
	
	/**
	 * 
	 */
	private final IConnectionHandler handler;
	
	/**
	 * @param tableViewer
	 */
	public TableHeaderSelectionListener(TableViewer tableViewer, IConnectionHandler connectionHandler) {
		this.tableViewer = tableViewer;
		handler = connectionHandler;
	}
	
	/**
	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent event) {
		final TableColumn selectedColumn = (TableColumn)event.widget;
		final Table table = selectedColumn.getParent();
		int sortDirection = table.getSortDirection();
		if(table.getSortColumn() == selectedColumn) {
			sortDirection = sortDirection == SWT.UP ? SWT.DOWN : SWT.UP;
		} else {
			table.setSortColumn(selectedColumn);
			sortDirection = SWT.UP;
		}
		table.setSortDirection(sortDirection);
		tableViewer.getSorter().sort(tableViewer, handler.getAvailableConnections());
		tableViewer.refresh();
	}

}
