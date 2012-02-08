package org.vclipse.configscan.injection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.util.Files;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;

import com.sap.conn.jco.JCoException;

public class MockConfigScanRunner implements ITestConfigScanRunner {

	private IFile file;
	
	public void setFile(IFile file) {
		this.file = file;
	}
	
	@Override
	public String execute(String output, RemoteConnection remoteConnection, String materialNumber) throws JCoException, CoreException {
		if(file == null) {
			return "";
		}
		
		//GetSelectedFileRunnable run = new GetSelectedFileRunnable();
		System.err.println("MockConfigScanRunner: executing " + file.getName()); 
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(remoteConnection.getDescription().equals("IPC D75")) {
			return Files.readFileIntoString(file.getLocation().toPortableString() + ".ipcd75.xml.log");
		} else if(remoteConnection.getDescription().equals("IPC D79")) {
			return Files.readFileIntoString(file.getLocation().toPortableString() + ".ipcd79.xml.log");
		}

		return Files.readFileIntoString(file.getLocation().toPortableString() + ".xml.log");
	}
}

class GetSelectedFileRunnable implements Runnable {

	public IFile selectedFile;

	@Override
	public void run() {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		if(selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection)selection).getFirstElement();
			if(firstElement instanceof IFile) {
				selectedFile = (IFile)firstElement;
			}
		}
	}
}