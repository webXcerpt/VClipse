/*******************************************************************************
 * Copyright (c) 2009 webXcerpt Software GmbH (http://www.webxcerpt.com).
 * All rights reserved.
 *******************************************************************************/
package org.vclipse.idoc2jcoidoc;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.vclipse.connection.IConnectionHandler;
import com.sap.conn.idoc.IDocDocument;

public interface IIDocsSender {

	public IStatus send(List<IDocDocument> idocs, IConnectionHandler handler, IProgressMonitor monitor);
}
