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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.vclipse.connection.IConnection;
import org.vclipse.connection.IConnectionHandler;

import com.sap.conn.jco.JCoException;

/**
 *
 */
final class ConnectionStateDialog extends ProgressMonitorDialog {

	/**
	 * 
	 */
	private final IConnectionHandler connectionHandler;
	
	/**
	 * 
	 */
	private IStatus status;
	
	/**
	 * @param parent
	 * @param handler
	 */
	public ConnectionStateDialog(final Shell parent, final IConnectionHandler handler) {
		super(parent);
		connectionHandler = handler;
		super.setCancelable(false);
		super.setOpenOnRun(true);
	}

	/**
	 * @throws Throwable 
	 */
	public IStatus connect(final IConnection connection) throws Throwable {
		try {
			super.run(true, false, new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Connecting with '" + connection.getSystemName() + "' ...", 10);
					try {
						status = connectionHandler.connect(connection);
					} catch(final JCoException exception) {
						monitor.done();
						throw new InvocationTargetException(exception);
					}
				}
			});
			return status;
		} catch (InvocationTargetException exception) {
			throw exception.getTargetException();
		} catch (InterruptedException exception) {
			throw exception;
		}
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.ProgressMonitorDialog#run(boolean, boolean, org.eclipse.jface.operation.IRunnableWithProgress)
	 */
	@Override
	@Deprecated
	public void run(final boolean fork, final boolean cancelable, final IRunnableWithProgress runnable) throws InvocationTargetException, InterruptedException {
		super.run(fork, cancelable, runnable);
	}

	/**
	 * @see org.eclipse.jface.dialogs.ProgressMonitorDialog#setCancelable(boolean)
	 */
	@Override
	@Deprecated
	public void setCancelable(final boolean cancelable) {
		super.setCancelable(cancelable);
	}

	/**
	 * @see org.eclipse.jface.dialogs.ProgressMonitorDialog#setOpenOnRun(boolean)
	 */
	@Override
	@Deprecated
	public void setOpenOnRun(final boolean openOnRun) {
		super.setOpenOnRun(openOnRun);
	}
}
