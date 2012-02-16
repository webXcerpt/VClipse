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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.CancelIndicator;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.IConfigScanRemoteConnections;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.extension.ExtensionPointReader;
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.utils.TestCaseFactory;
import org.vclipse.configscan.views.ConfigScanView;
import org.vclipse.configscan.views.ConfigScanViewInput;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class LaunchDelegate extends LaunchConfigurationDelegate {

	@Inject
	private IConfigScanRemoteConnections remoteConnections;
	
	@Inject
	private ExtensionPointReader extensionPointReader;
	
	@Inject
	private TestCaseFactory testCaseFactory;
	
	@Inject
	private DocumentUtility documentUtility;
	
	@SuppressWarnings("unchecked")
	@Override
	public void launch(final ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Sending test cases to ConfigScan", IProgressMonitor.UNKNOWN);
		monitor.subTask("Reading available remote connections");
		List<? extends RemoteConnection> remoteConnectionsList = Lists.newArrayList();
		try {
			remoteConnectionsList = remoteConnections.readConfigScanRemoteConnections();
		} catch (JCoException exception) {
			ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
		}
		
		monitor.subTask("Collecting selected connections");
		Map<String, RemoteConnection> selectedConnections = Maps.newHashMap();
		Map<?, ?> attributes = configuration.getAttributes();
		if(!attributes.isEmpty()) {
			Set<String> selectedConnectionsNames = Sets.newHashSet();
			for(Object key : attributes.keySet()) {
				if(key instanceof String) {
					Object object = attributes.get(key);
					if(object instanceof Boolean && (Boolean)object) {
						selectedConnectionsNames.add((String)key);
					}
				}
			}
			for(RemoteConnection rc : remoteConnectionsList) {
				String name = rc.getDescription();
				if(selectedConnectionsNames.contains(name)) {
					selectedConnections.put(name, rc);
				}
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
				final List<TestRun> testRuns = Lists.newArrayList();
				while(iterator.hasNext()) {
					Object nextObject = iterator.next();
					if(nextObject instanceof IFile) {
						IFile currentFile = (IFile)nextObject;
						String extension = currentFile.getFileExtension();
						String fileName = currentFile.getName();
						testCaseFactory.setOptions((Map<Object, Object>)attributes);
						
						if(!extensionPointReader.hasExtensionFor(extension)) {
							for(RemoteConnection connection : selectedConnections.values()) {
								testRuns.add(testCaseFactory.buildTestRun(fileName, connection, documentUtility.parse(currentFile.getContents()), null));
							}
						} else {
							Resource currentResource = resourceSet.getResource(URI.createURI(currentFile.getLocationURI().toString()), true);
							EObject testModel = currentResource.getContents().get(0);
							EcoreUtil2.resolveAll(currentResource, CancelIndicator.NullImpl);
							for(RemoteConnection connection : selectedConnections.values()) {
								IConfigScanXMLProvider xmlProvider = extensionPointReader.getXmlProvider(extension);
								testRuns.add(testCaseFactory.buildTestRun(fileName, connection, xmlProvider, testModel));									
							}							
						}
					}
				}
				
				final ConfigScanViewInput input = new ConfigScanViewInput();
				input.setConfigurationName(configuration.getName());
				input.setTestRuns(testRuns);
				input.setDate(null, IConfigScanConfiguration.DATE_FORMAT_UI_ENTRIES);
				
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						try {
							ConfigScanView view = (ConfigScanView)activePage.showView(ConfigScanView.ID);
							view.setInput(input);
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
			} else if(selection instanceof ITextSelection) {
				IEditorPart activeEditor = activePage.getActiveEditor();
				if(activeEditor instanceof XtextEditor) {
					IEditorInput editorInput = ((XtextEditor)activeEditor).getEditorInput();
					if(editorInput instanceof IFileEditorInput) {
						strSelection = new StructuredSelection(((IFileEditorInput)editorInput).getFile());
					}
				}
			}
		}
	}
}
