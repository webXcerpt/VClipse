package org.vclipse.configscan.injection;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.WrappedException;
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
	public String execute(String output, RemoteConnection remoteConnection, String materialNumber, Map<String, Object> options) throws JCoException, CoreException {
		GetSelectedFileRunnable run = new GetSelectedFileRunnable();
		Display.getDefault().asyncExec(run);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException exception) {
			exception.printStackTrace();
		}

		if(run.selectedFile == null) {
			return "";
		}
		
		System.err.println("MockConfigScanRunner: executing " + run.selectedFile.getName());
		if(remoteConnection.getDescription().equals("IPC D75")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".ipcd75.xml.log");	
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("IPC D79")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".ipcd79.xml.log");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("IPC P75")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".ipcp75.xml.log");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("IPC P79")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".ipcp79.xml.log");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("IPC Q75")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".ipcq75.xml.log");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("IPC Q79")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".ipcq79.xml.log");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("VC D75")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".vcd75.xml.log");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("VC P75")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".vcp75.xml.log");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("VC Q75")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".vcq75.xml.log");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		}
		// default file is name without cmlt/vcmlt
		return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString().replace(".cmlt", "").replace(".vcmlt", "") + ".xml.log");
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
		} catch (PartInitException exception) {
			exception.printStackTrace();
		}
	}
}