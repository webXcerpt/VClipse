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
package org.vclipse.idoc2jcoidoc.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vclipse.connection.IConnection;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.IConnectionListener;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.IUiConstants;
import org.vclipse.idoc2jcoidoc.RFCIDocsSender;
import org.vclipse.idoc2jcoidoc.internal.UserIDocsSender;
import org.vclipse.idoc2jcoidoc.views.IDocView.IDocViewInput;

/**
 *
 */
public class SendIDocsAction extends Action implements IConnectionListener {

	/**
	 * 
	 */
	private static final String NOT_CONNECTED_MESSAGE = "Not connected to any SAP system";

	/**
	 * 
	 */
	private final TreeViewer treeViewer;
	
	/**
	 * 
	 */
	private final IConnectionHandler connectionHandler;
	
	/**
	 * 
	 */
	private final IPreferenceStore preferenceStore;
	
	/**
	 * @param page
	 */
	public SendIDocsAction(TreeViewer treeViewer, IConnectionHandler connectionHandler, IPreferenceStore preferenceStore) {
		this.connectionHandler = connectionHandler;
		this.preferenceStore = preferenceStore;
		this.treeViewer = treeViewer;
		IConnection currentConnection = connectionHandler.getCurrentConnection();
		if(currentConnection != null) {
			connected(currentConnection);
		} else {
			disconnected();
		}
		setText("Send IDocs action");
		setImageDescriptor(IDoc2JCoIDocPlugin.getImageDescriptor(IUiConstants.SEND_IDOCS_IMAGE));
		setDisabledImageDescriptor(IDoc2JCoIDocPlugin.getImageDescriptor(IUiConstants.SEND_IDOCS_IMAGE_DISABLED));
		connectionHandler.addConnectionListener(this);
	}
	
	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		final Shell shell = treeViewer.getTree().getShell();
		String title = "Send IDocs to the SAP system?";
		IConnection connection = connectionHandler.getCurrentConnection();
		String sapSystemName = connection.getSystemName();
		sapSystemName = sapSystemName.isEmpty() ? "unknown" : sapSystemName;
		String message = "Do you really want to send IDocs to the SAP system '" + sapSystemName + "'?";
		MessageDialog messageDialog = new MessageDialog(shell, title, null, message, MessageDialog.QUESTION, new String[]{"Yes", "No"}, 0);
		if(messageDialog.open() == Window.OK) {
			String senderType = preferenceStore.getString(IUiConstants.NUMBERS_PROVIDER);
			IDocsSender sender = null;
			if(IUiConstants.TARGET_SYSTEM.equals(senderType)) {
				sender = new RFCIDocsSender();
			} else {
				sender = new UserIDocsSender(shell);
			}
			runSenderJob(sender, ((IDocContentProvider)treeViewer.getContentProvider()).getInput());
		}
	}

	/**
	 * @param sender
	 * @param input
	 */
	private void runSenderJob(final IDocsSender sender, final IDocViewInput input) {
		Job job = new Job("Sending IDocs to '" + connectionHandler.getCurrentConnection().getSystemName() + "'...") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				setEnabled(false);
				IStatus status = sender.send(input.getDocuments(), connectionHandler, monitor);
				setEnabled(true);
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						treeViewer.refresh(true);
					}
				});
				return status;
			}
		};
		job.setPriority(Job.LONG);
		job.schedule();
	}

	/**
	 * @see org.vclipse.connection.ISapConnectionListener#connected(org.vclipse.connection.SapConnection)
	 */
	@Override
	public void connected(IConnection connection) {
		setToolTipText("Send IDocs to '" + connection.getSystemName() + "'");
		setEnabled(treeViewer.getTree().getItemCount() > 0);
	}

	/**
	 * @see org.vclipse.connection.ISapConnectionListener#disconnected()
	 */
	@Override
	public void disconnected() {
		setToolTipText(NOT_CONNECTED_MESSAGE);
		setEnabled(false);
	}
}
