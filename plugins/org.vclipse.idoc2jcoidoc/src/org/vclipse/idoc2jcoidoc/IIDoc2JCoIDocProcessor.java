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
package org.vclipse.idoc2jcoidoc;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.vclipse.idoc.iDoc.Model;

import com.google.inject.ImplementedBy;
import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.jco.JCoException;

@ImplementedBy(DefaultIDoc2JCoIDocProcessor.class)
public interface IIDoc2JCoIDocProcessor {

	public List<IDocDocument> transform(Model idocModel, IProgressMonitor monitor) throws JCoException, CoreException;
}
