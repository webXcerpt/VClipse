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
package org.vclipse.configscan.launch;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.Pair;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanRemoteConnections;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.extension.ExtensionPointReader;
import org.vclipse.configscan.implementation.ConfigScanTestRun;
import org.vclipse.configscan.views.ConfigScanView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class LaunchDelegate extends LaunchConfigurationDelegate {

	private static final String MISSING_EXTENSION_WARNING_MESSAGE = "There is no registered xml provider for files with extension ";

	@Inject
	private IConfigScanRemoteConnections remoteConnections;
	
	@Inject
	private ExtensionPointReader extensionPointReader;
	
	@Override
	public void launch(final ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		final Map<String, Pair<IConfigScanXMLProvider, ILabelProvider>> extensions = extensionPointReader.getExtensions();
		
		monitor.beginTask("Sending test cases to ConfigScan", IProgressMonitor.UNKNOWN);
		monitor.subTask("Reading available remote connections");
		List<? extends RemoteConnection> remoteConnectionsList = Lists.newArrayList();
		try {
			remoteConnectionsList = remoteConnections.readConfigScanRemoteConnections();
		} catch (JCoException e) {
			e.printStackTrace();
		}
		
		monitor.subTask("Collecting selected connections");
		Map<String, RemoteConnection> selectedConnections = Maps.newHashMap();
		Map<?, ?> attributes = configuration.getAttributes();
		if(!attributes.isEmpty()) {
			String prefix = configuration.getName() + ManyConnectionsTab.SEPARATOR;
			Set<String> names = Sets.newHashSet();
			for(Object key : attributes.keySet()) {
				if(key instanceof String && ((String)key).startsWith(prefix)) {
					names.add(((String)key).replace(prefix, ""));
				}
			}
			for(RemoteConnection rc : remoteConnectionsList) {
				String name = rc.getDescription();
				if(names.contains(name)) {
					selectedConnections.put(name, rc);
				}
			}
		} else {
			int attribute = configuration.getAttribute(OneConnectionTab.CURRENT_CONNECTION_INDEX, 0);
			if(attribute < remoteConnectionsList.size()) {
				RemoteConnection rc = remoteConnectionsList.get(attribute);
				selectedConnections.put(rc.getDescription(), rc);
			}
		}
		
		if(selectedConnections.isEmpty()) {
			monitor.done();
		} else {
			GetSelectionRunnable runnable = new GetSelectionRunnable();
			Display.getDefault().syncExec(runnable);
			final IWorkbenchPage activePage = runnable.activePage;
			IStructuredSelection selection = runnable.strSelection;
			if(selection == null) {
				ConfigScanPlugin.log("No files are selected for test runs", IStatus.WARNING);
			} else {
				Iterator<?> iterator = selection.iterator();
				XtextResourceSet resourceSet = new XtextResourceSet();
				final List<ConfigScanTestRun> testRuns = Lists.newArrayList();
				while(iterator.hasNext()) {
					Object next = iterator.next();
					if(next instanceof IFile) {
						IFile currentFile = (IFile)next;
						String fileExtension = currentFile.getFileExtension();
						
						// search for the extension
						if(!extensions.containsKey(fileExtension)) {
							ConfigScanPlugin.log(MISSING_EXTENSION_WARNING_MESSAGE + fileExtension, IStatus.WARNING);
						} else {
							IConfigScanXMLProvider xmlProvider = extensions.get(fileExtension).getFirst();
							URI uri = URI.createURI(currentFile.getLocationURI().toString());
							Resource currentResource = resourceSet.getResource(uri, true);
							
							if(currentResource == null) {
								ConfigScanPlugin.log("Can not create a resource for the file " + currentFile.getName(), IStatus.WARNING);
								continue;
							} else {
								for(RemoteConnection rc : selectedConnections.values()) {
									ConfigScanTestRun newTestRun = ConfigScanPlugin.getDefault().getInjector().getInstance(ConfigScanTestRun.class);
									newTestRun.setTestModel(currentResource.getContents().get(0));
									newTestRun.setXmlProvider(xmlProvider);
									newTestRun.setConnection(rc);
									testRuns.add(newTestRun);
								}
							}
						}
					}
				}
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						try {
							ConfigScanView view = (ConfigScanView)activePage.showView(ConfigScanView.ID);
							view.setInput(testRuns);
						} catch (PartInitException exception) {
							ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
						}
					}
				});
			}
		}
	}
}

class GetSelectionRunnable implements Runnable {
	IStructuredSelection strSelection;
	IWorkbenchPage activePage;
	@Override
	public void run() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if(window != null) {
			activePage = window.getActivePage();
			ISelection selection = activePage.getSelection();
			if(selection instanceof IStructuredSelection) {
				strSelection = (IStructuredSelection)selection;
			}
		}
	}
}
