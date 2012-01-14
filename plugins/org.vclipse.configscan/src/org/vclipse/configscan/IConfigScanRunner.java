package org.vclipse.configscan;
/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/


import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.configscan.impl.ConfigScanRunner;

import com.google.inject.ImplementedBy;
import com.sap.conn.jco.JCoException;

@ImplementedBy(ConfigScanRunner.class)
public interface IConfigScanRunner {

	public String execute(String output, RemoteConnection remoteConnection, String materialNumber, IFile file) throws JCoException, CoreException;

}
