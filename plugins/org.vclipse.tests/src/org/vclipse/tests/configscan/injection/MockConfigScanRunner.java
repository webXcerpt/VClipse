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
package org.vclipse.tests.configscan.injection;

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
	public String execute(String output, RemoteConnection remoteConnection, String materialNumber, Map<Object, Object> options) throws JCoException, CoreException {
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
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".IPC D75." + "log.xml");	
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("IPC D79")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".IPC D79."+ "log.xml");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("IPC P75")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".IPC P75." + "log.xml");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("IPC P79")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".IPC P79." + "log.xml");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("IPC Q75")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".IPC Q75." + "log.xml");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("IPC Q79")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".IPC Q79." + "log.xml");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("VC D75")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".VC D75." + "log.xml");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("VC P75")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".VC P75." + "log.xml");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		} else if(remoteConnection.getDescription().equals("VC Q75")) {
			try {
				return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString() + ".VC Q75." + "log.xml");
			} catch(WrappedException exception) {
				exception.printStackTrace();
			}
		}
		// default file is name without cmlt/vcmlt
		return Files.readFileIntoString(run.selectedFile.getLocation().toPortableString().replace(".cmlt", "").replace(".vcmlt", "") + ".log.xml");
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
			/** the implementation above is absolutely sufficien for test purposes !
			 * 
			 * else if(selection instanceof ITextSelection) {
				IEditorPart activeEditor = activePage.getActiveEditor();
				if(activeEditor instanceof XtextEditor) {
					IEditorInput editorInput = ((XtextEditor)activeEditor).getEditorInput();
					if(editorInput instanceof IFileEditorInput) {
						strSelection = new StructuredSelection(((IFileEditorInput)editorInput).getFile());
					}
				}
			}
			 */
		} catch (PartInitException exception) {
			exception.printStackTrace();
		}
	}
}