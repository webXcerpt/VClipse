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
package org.vclipse.configscan.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanRemoteConnections;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.configscan.IConfigScanRunner;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.views.ConfigScanView;
import org.vclipse.configscan.views.XmlLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class CSLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	private static final String MISSING_EXTENSION_WARNING_MESSAGE = "There is no registered xml provider for files with extension ";

	// the id of the extension point
	private static final String XML_PROVIDER_EXTENSION_POINT_ID = "org.vclipse.configscan.xmlProvider";
	
	// the name of the configuration element we are interested in
	private static final String CONFIGURATION_ELEMENT_NAME = "xmlprovider";
	
	// attributes provided by the extension point
	private static final String ATTRIBUTE_CLASS = "class";
	private static final String ATTRIBUTE_FILE_EXTENSION = "file_extension";
	
	@Inject
	private IConfigScanRunner runner;	
	
	@Inject
	private IConfigScanRemoteConnections remoteConnections;
	
	@Inject
	private XmlLoader xmlLoader;
	
	@Inject
	private IConfigScanXMLProvider xmlProvider;
	
	// xml providers available through the extension point
	private Map<String, IConfigScanXMLProvider> availableXmlProvider;
	
	public CSLaunchConfigurationDelegate() {
		availableXmlProvider = Maps.newHashMapWithExpectedSize(10);
	}
	
	@Override
	public void launch(final ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		// read the extension point
		if(availableXmlProvider.isEmpty()) {
			readXmlProviderExtension();
		}
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if(window != null) {
					final IWorkbenchPage page = window.getActivePage();
					final ISelection selection = page.getSelection();
					if(selection instanceof IStructuredSelection) {
						// handle only the first selected object
						Object selectedElement = ((IStructuredSelection)selection).getFirstElement();
						if(selectedElement instanceof IFile) {
							final IFile currentFile = (IFile)selectedElement;
							final String fileExtension = currentFile.getFileExtension();
							// do we have a registered xml provider for this file extension?
							if(!availableXmlProvider.containsKey(fileExtension)) {
								RuntimeException missingExtension = new RuntimeException(MISSING_EXTENSION_WARNING_MESSAGE + fileExtension);
								ConfigScanPlugin.log(missingExtension.getMessage(), IStatus.WARNING, missingExtension);
							} else {
								Job configScanTestJob = new Job("Executing test cases with ConfigScan") {
									@Override
									protected IStatus run(IProgressMonitor monitor) {
										monitor.beginTask("Sending test cases to ConfigScan", IProgressMonitor.UNKNOWN);
										xmlProvider = availableXmlProvider.get(fileExtension);
										monitor.subTask("Loading file " + currentFile.getName());
										URI currentUri = URI.createURI(currentFile.getLocationURI().toString());
										
										Resource currentResource = new XtextResourceSet().getResource(currentUri, true);
										if(currentResource == null) {
											monitor.done();
											return new Status(IStatus.ERROR, ConfigScanPlugin.ID, "Could not load a resource for the file " + currentFile.getName());
										}

										EList<EObject> contents = currentResource.getContents();
										if(contents.isEmpty()) {
											monitor.done();
											return new Status(IStatus.ERROR, ConfigScanPlugin.ID, "Contents of the file " + currentFile.getName() + " are empty");
										}

										try {
											int connectionIndex = configuration.getAttribute(OptionsLaunchConfigurationTab.CURRENT_CONNECTION_INDEX, 0);
											List<? extends RemoteConnection> remoteConnectionsList = remoteConnections.readConfigScanRemoteConnections();
											if(connectionIndex < remoteConnectionsList.size()) {
												RemoteConnection remoteConnection = remoteConnectionsList.get(connectionIndex);
												final HashMap<Element, URI> inputToUriMap = new HashMap<Element, URI>();
												final Document xmlInputDocument = xmlProvider.transform(contents.get(0), inputToUriMap);
												String xmlLog = runner.execute(currentFile, xmlLoader.parseXmlToString(xmlInputDocument), 
														remoteConnection, xmlProvider.getMaterialNumber(contents.get(0)));
												
												
												final Document xmlLogDocument = xmlLoader.parseXmlString(xmlLog);
												final Map<Element, Element> mapLogInput = xmlProvider.computeConfigScanMap(xmlLogDocument, xmlInputDocument);


												Display.getDefault().syncExec(new Runnable() {
													@Override
													public void run() {
														ConfigScanView view;
														try {
															view = (ConfigScanView)page.showView(ConfigScanView.ID);
															view.setInput(xmlLogDocument, xmlInputDocument, mapLogInput, inputToUriMap);
														} catch (PartInitException exception) {
															ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR, exception.getCause());
														}
													}
												});
											}
										} catch (JCoException exception) {
											ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR, exception.getCause());
										} catch (CoreException exception) {
											ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR, exception.getCause());
										}
										monitor.done();
										return Status.OK_STATUS;
									}
								};
								configScanTestJob.schedule();
							}
						}
					}
				}
			}
		});
	};
	
	// reads the extension point
	private void readXmlProviderExtension() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(XML_PROVIDER_EXTENSION_POINT_ID);
		for(IExtension extension : extensionPoint.getExtensions()) {
			for(IConfigurationElement configurationElement : extension.getConfigurationElements()) {
				if(CONFIGURATION_ELEMENT_NAME.equals(configurationElement.getName())) {
					String fileExtension = configurationElement.getAttribute(ATTRIBUTE_FILE_EXTENSION);
					if(fileExtension != null && !fileExtension.isEmpty()) {
						// only one xml provider for one file extension is allowed
						if(availableXmlProvider.containsKey(fileExtension)) {
							continue;
						}
						
						try {
							Object classObject = configurationElement.createExecutableExtension(ATTRIBUTE_CLASS);
							if(classObject instanceof IConfigScanXMLProvider) {
								availableXmlProvider.put(fileExtension, (IConfigScanXMLProvider)classObject);
							}
						} catch (CoreException exception) {
							// log the error
							ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR, exception.getCause());
							
							// handle the next extension
							continue;
						}
					}
				}
			}
		}
	}
}
