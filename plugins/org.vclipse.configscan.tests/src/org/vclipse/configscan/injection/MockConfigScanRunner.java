package org.vclipse.configscan.injection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.util.Files;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.configscan.IConfigScanRunner;

import com.sap.conn.jco.JCoException;

@SuppressWarnings("restriction")
public class MockConfigScanRunner implements IConfigScanRunner {

	@Override
	public String execute(String output, RemoteConnection remoteConnection, String materialNumber) throws JCoException, CoreException {
		GetSelectedFileRunnable run = new GetSelectedFileRunnable();
		Display.getDefault().asyncExec(run);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.err.println("MockConfigScanRunner: executing " + run.selectedFile.getName()); 
		if(remoteConnection.getDescription().equals("IPC D75")) {
			return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".ipcd75.xml.log");
		} else if(remoteConnection.getDescription().equals("IPC D79")) {
			return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".ipcd79.xml.log");
		}

		return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".xml.log");
	}
}

@SuppressWarnings("restriction")
class GetSelectedFileRunnable implements Runnable {

	public IFile selectedFile;

	@Override
	public void run() {
		try {
			IViewPart showView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.jdt.ui.PackageExplorer");
			if(showView instanceof PackageExplorerPart) {
				ISelection selection = ((PackageExplorerPart)showView).getTreeViewer().getSelection();
						if(selection instanceof IStructuredSelection) {
							Object firstElement = ((IStructuredSelection)selection).getFirstElement();
							if(firstElement instanceof IFile) {
								selectedFile = (IFile)firstElement;
							}
						}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}