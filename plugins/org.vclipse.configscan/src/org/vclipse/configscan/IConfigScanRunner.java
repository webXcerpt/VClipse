package org.vclipse.configscan;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;

import com.google.inject.ImplementedBy;
import com.sap.conn.jco.JCoException;

@ImplementedBy(ConfigScanRunner.class)
public interface IConfigScanRunner {

	public String execute(IFile file, String xmlString, RemoteConnection rc, String matNr, String bomApplication) throws JCoException, CoreException;

}
