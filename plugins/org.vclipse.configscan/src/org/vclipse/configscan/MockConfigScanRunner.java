package org.vclipse.configscan;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.xtext.util.Files;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;

import com.sap.conn.jco.JCoException;

public class MockConfigScanRunner implements IConfigScanRunner {

	public String execute(IFile file, String xmlInput, RemoteConnection rc, String matNr) throws JCoException, CoreException {
		
		System.err.println("MockConfigScanRunner: executing " + file.getName()); 
		
//		return "TODO: dies ist zu ersetzen"; // TODO Inhalt der Datei file.getName() + ".log" zurückgeben
		
//		File f = new File(file.getFullPath().toPortableString() + ".log");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Files.readFileIntoString(file.getLocation().toPortableString() + ".xml.log");
		
//		return Files.readStreamIntoString(file.getContents());
	}

}
