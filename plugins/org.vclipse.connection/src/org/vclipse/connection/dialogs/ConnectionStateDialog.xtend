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

import java.lang.reflect.InvocationTargetException
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.IStatus
import org.eclipse.jface.dialogs.ProgressMonitorDialog
import org.eclipse.jface.operation.IRunnableWithProgress
import org.eclipse.swt.widgets.Shell
import org.vclipse.connection.IConnection
import org.vclipse.connection.IConnectionHandler
import com.sap.conn.jco.JCoException

/** 
 */
final package class ConnectionStateDialog extends ProgressMonitorDialog {
	/** 
	 */
	final IConnectionHandler connectionHandler
	/** 
	 */
	IStatus status

	/** 
	 * @param parent
	 * @param handler
	 */
	new(Shell parent, IConnectionHandler handler) {
		super(parent)
		connectionHandler = handler
		super.setCancelable(false)
		super.setOpenOnRun(true)
	}

	/** 
	 * @throws Throwable 
	 */
	def IStatus connect(IConnection connection) throws Throwable {
		try {
			super.run(true, false, [ IProgressMonitor monitor |
				monitor.beginTask('''Connecting with '«»«connection.getSystemName()»' ...''', 10)
				try {
					status = connectionHandler.connect(connection)
				} catch (JCoException exception) {
					monitor.done()
					throw new InvocationTargetException(exception)
				}
			])
			return status
		} catch (InvocationTargetException exception) {
			throw exception.getTargetException()
		} catch (InterruptedException exception) {
			throw exception
		}

	}

	/** 
	 * @see org.eclipse.jface.dialogs.ProgressMonitorDialog#run(boolean, boolean, org.eclipse.jface.operation.IRunnableWithProgress)
	 */
	@Deprecated override void run(boolean fork, boolean cancelable,
		IRunnableWithProgress runnable) throws InvocationTargetException, InterruptedException {
		super.run(fork, cancelable, runnable)
	}

	/** 
	 * @see org.eclipse.jface.dialogs.ProgressMonitorDialog#setCancelable(boolean)
	 */
	@Deprecated override void setCancelable(boolean cancelable) {
		super.setCancelable(cancelable)
	}

	/** 
	 * @see org.eclipse.jface.dialogs.ProgressMonitorDialog#setOpenOnRun(boolean)
	 */
	@Deprecated override void setOpenOnRun(boolean openOnRun) {
		super.setOpenOnRun(openOnRun)
	}

}
