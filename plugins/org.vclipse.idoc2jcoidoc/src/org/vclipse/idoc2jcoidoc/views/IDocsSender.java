/*******************************************************************************
 * Copyright (c) 2009 webXcerpt Software GmbH (http://www.webxcerpt.com).
 * All rights reserved.
 *******************************************************************************/
package org.vclipse.idoc2jcoidoc.views;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Shell;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.idoc2jcoidoc.IDocSenderStatus;
import org.vclipse.idoc2jcoidoc.IIDocsSender;

import com.sap.conn.idoc.IDocDocument;

/**
 * 
 */
public abstract class IDocsSender implements IIDocsSender {
	
	public IDocSenderStatus cancelStatus = new IDocSenderStatus(IStatus.CANCEL);
	
	public IDocSenderStatus completeStatus = new IDocSenderStatus(IStatus.OK);
	
	/**
	 * 
	 */
	protected Shell parentShell;
	
	/**
	 *  
	 */
	public IDocsSender() {
		this(null);
	}
	
	/**
	 * @param shell
	 */
	public IDocsSender(Shell shell) {
		parentShell = shell;
	}
	
	/**
	 * @see org.vclipse.idoc2jcoidoc.IIDocsSender#send(java.util.List, org.vclipse.connection.ISapConnectionHandler, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public abstract IDocSenderStatus send(List<IDocDocument> idocs, IConnectionHandler handler, IProgressMonitor monitor);
}
