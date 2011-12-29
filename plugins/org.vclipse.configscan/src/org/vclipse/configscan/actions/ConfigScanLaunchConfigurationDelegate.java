package org.vclipse.configscan.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
import org.vclipse.configscan.Activator;
import org.vclipse.configscan.IConfigScanRemoteConnections;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.configscan.IConfigScanRunner;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.views.XmlLoader;
import org.vclipse.configscan.views.ConfigScanView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class ConfigScanLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	private static final String CONFIGSCAN_VIEW_ID = Activator.PLUGIN_ID + ".views.ConfigScanView";
	
	private static final String CMLT_EXTENSION = "cmlt";
	
	@Inject
	private IConfigScanRunner runner;
	
	@Inject
	private IConfigScanRemoteConnections remoteConnections;
	
	@Inject
	private IConfigScanXMLProvider xmlProvider;
	
	@Override
	public void launch(final ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					if(window != null) {
						IWorkbenchPage page = window.getActivePage();
						ISelection selection = page.getSelection();
						if(selection instanceof IStructuredSelection) {
							final Iterator<?> iterator = ((IStructuredSelection)selection).iterator();
							final ConfigScanView view = (ConfigScanView) page.showView(CONFIGSCAN_VIEW_ID);
							Job job = new Job("Executing test cases with ConfigScan") {
								@Override
								protected IStatus run(IProgressMonitor monitor) {
									monitor.beginTask("Sending test cases to ConfigScan", IProgressMonitor.UNKNOWN);
									while(iterator.hasNext()) {
										Object nextObject = iterator.next();
										if(nextObject instanceof IFile) {
											IFile file = (IFile)nextObject;
											if(CMLT_EXTENSION.equals(file.getFileExtension())) {
												monitor.subTask("Loading CmlT file " + file.getName());
												System.err.println("CmlT file: " + file.getName());
												try {					  
													int index = configuration.getAttribute(OptionsLaunchConfigurationTab.CURRENT_CONNECTION_INDEX, 0);
													List<? extends RemoteConnection> readConfigScanRemoteConnections = remoteConnections.readConfigScanRemoteConnections();
													if(index > -1 && readConfigScanRemoteConnections.size() > 0) {
														RemoteConnection remoteConnection = readConfigScanRemoteConnections.get(index);
														
														XmlLoader xmlLoader = new XmlLoader();
														
														final Map<Element, URI> inputToUri = new HashMap<Element, URI>();
														
														
														
														final Resource res = new XtextResourceSet().getResource(URI.createURI(file.getLocationURI().toString()), true);
														if (res == null) {
															throw new IllegalArgumentException("resource null");
														}
														EList<EObject> contents = res.getContents();
														if (contents.size()==0) {
															throw new IllegalArgumentException("no contents");
														}
														
														final Document xmlInputDoc = xmlProvider.transform(contents.get(0), inputToUri);		// inputToUri is filled here
														
														String output = xmlLoader.parseXmlToString(xmlInputDoc);
														

														final String xmlLog = runner.execute(file, output, remoteConnection, xmlProvider.getMaterialNumber(contents.get(0)), xmlProvider.getBomApplication(contents.get(0)));
														
														final Document xmlLogDoc = xmlLoader.parseXmlString(xmlLog);
														
														
														
														
														final Map<Element, Element> mapLogInput = xmlProvider.computeConfigScanMap(xmlLogDoc, xmlInputDoc);
							
														
														Display.getDefault().syncExec(new Runnable() {
															@Override
															public void run() {
																view.setInput(xmlLogDoc, xmlInputDoc, mapLogInput, inputToUri);
															}
														});
													}
												} catch (JCoException e) {
													e.printStackTrace();
												} catch (CoreException e) {
													e.printStackTrace();
												}
												monitor.done();
												return Status.OK_STATUS;
											}
										}
									}
									return Status.CANCEL_STATUS;
								}
							};
							job.schedule();
						}
					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
