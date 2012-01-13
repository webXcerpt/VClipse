package org.vclipse.configscan;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.util.Files;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;

import com.sap.conn.jco.JCoException;

public class MockConfigScanRunner implements IConfigScanRunner {

	public String execute(String xmlInput, RemoteConnection rc, String matNr, IFile file) throws JCoException, CoreException {
		//GetSelectedFileRunnable run = new GetSelectedFileRunnable();
		
		System.err.println("MockConfigScanRunner: executing " + file.getName()); 
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
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