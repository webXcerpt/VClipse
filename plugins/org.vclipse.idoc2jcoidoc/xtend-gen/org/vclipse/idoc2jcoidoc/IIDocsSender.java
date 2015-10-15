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
package org.vclipse.idoc2jcoidoc;

import com.sap.conn.idoc.IDocDocument;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.vclipse.connection.IConnectionHandler;

@SuppressWarnings("all")
public interface IIDocsSender {
  public abstract IStatus send(final List<IDocDocument> idocs, final IConnectionHandler handler, final IProgressMonitor monitor);
}
