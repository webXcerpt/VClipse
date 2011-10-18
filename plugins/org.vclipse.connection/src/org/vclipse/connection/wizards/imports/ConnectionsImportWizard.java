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
package org.vclipse.connection.wizards.imports;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.connection.internal.AbstractConnection;

import com.google.inject.Inject;

/**
 *	
 */
public final class ConnectionsImportWizard extends Wizard implements IImportWizard {

	/**
	 * Page for this wizard.
	 */
	private ConnectionsImportWizardPage page;
	
	/**
	 * 
	 */
	private final IConnectionHandler handler;
	
	/**
	 * @param connectionHandler
	 */
	@Inject
	public ConnectionsImportWizard(IConnectionHandler connectionHandler) {
		handler = connectionHandler;
	}
	
	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		setWindowTitle("Import wizard for SAP systems");
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		addPage(page = new ConnectionsImportWizardPage("page_one"));
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				final AbstractConnection[] connections = page.getSelectedConnections();
				monitor.beginTask("Importing SAP systems data...", connections.length);
				for(AbstractConnection connection : connections) {
					handler.addConnection(connection);
					monitor.worked(1);
				}
				handler.storeConnectionData();
				monitor.done();
			}
		};
		try {
			getContainer().run(false, true, runnable);
		} catch (InvocationTargetException exception) {
			VClipseConnectionPlugin.log(exception.getMessage(), exception.getTargetException());
			showErrorDialog(exception);
		} catch (InterruptedException e) {
			VClipseConnectionPlugin.log(e.getMessage(), e);
			showErrorDialog(e);
		}
		return true;
	}
	
	/**
	 * @param exception
	 */
	private void showErrorDialog(final Exception exception) {
		ErrorDialog.openError(getShell(), "Error", "Error on importing SAP systems", 
				new Status(IStatus.ERROR, VClipseConnectionPlugin.ID, exception.getMessage()));
	}
}
