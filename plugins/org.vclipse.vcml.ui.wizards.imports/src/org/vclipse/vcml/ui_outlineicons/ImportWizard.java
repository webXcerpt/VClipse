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
package org.vclipse.vcml.ui_outlineicons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.tools.ant.filters.StringInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.exports.FeatureExportInfo;
import org.eclipse.pde.internal.core.exports.PluginExportOperation;
import org.eclipse.pde.internal.ui.PDEPluginImages;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * Code partly adopted from the net.sf.rcer.jcoimport plug-in by Volker Wegert, see http://rcer.sourceforge.net/
 */
@SuppressWarnings("restriction")
public class ImportWizard extends Wizard implements IImportWizard {

	private static final List<String> GIF_IMAGE_NAMES = 
			Arrays.asList("b_slis.gif", "b_slip.gif", "s_chaa.gif", "b_aboa.gif", "b_rela.gif", "b_kons.gif",
			"b_class.gif", "b_conf.gif", "snapgr.gif", "magrid.gif", "b_text.gif", "b_anno.gif",
			"bwvisu.gif", "b_mbed.gif", "b_bedi.gif", "b_matl.gif", "b_bwfo.gif", "b_abal.gif",
			"dbtabl.gif");
	
	private static final String PLUGIN_NATURE_ID = "org.eclipse.pde.PluginNature";
	
	private static final String PROJECT_NAME = "org.vclipse.vcml.ui_sapicons";
	
	private Page page;
	
	public ImportWizard() {
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// not used
	}
	
	@Override
	public void addPages() {
		addPage(page = new Page("iconsPage"));
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						monitor.beginTask("Importing icons for VCML outline view...", IProgressMonitor.UNKNOWN);
						monitor.subTask("Creating new fragment project containing icons");
						IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
						if(project.exists()) {
							project.delete(true, true, monitor);
						}
						// create and open the project                                                                       
						project.create(monitor);
						project.open(monitor);
						monitor.worked(1);

						monitor.subTask("Creating project description");                                                                 
						IProjectDescription description = project.getDescription();
						description.setNatureIds(new String[] {	JavaCore.NATURE_ID, PLUGIN_NATURE_ID });
						project.setDescription(description, new SubProgressMonitor(monitor, 5));
						monitor.worked(1);

						monitor.subTask("Creating folder icons");
						IFolder iconsFolder = project.getFolder("icons");
						if(!iconsFolder.exists()) {
							iconsFolder.create(true, true, new SubProgressMonitor(monitor, 5));
						}
						monitor.worked(1);

						File jarFile = page.getJarFile();
						monitor.subTask("Reading and exporting images from the " + jarFile.getName() + " file");
						for(String key : readArchiveFile(jarFile).keySet()) {
							String[] split = key.split("/");
							String lastSection = split[split.length - 1];
							if(GIF_IMAGE_NAMES.contains(lastSection)) {
								IFile file = iconsFolder.getFile(lastSection);
								if(!file.exists()) {
									file.create(new ByteArrayInputStream(readArchiveFile(jarFile).get(key)), true, monitor);
								}
							}
							monitor.worked(1);
						}

						monitor.subTask("Creating META-INF folder and MANIFEST.MF file");
						IFolder metaInfFolder = project.getFolder("META-INF");
						metaInfFolder.create(true, true, monitor);
						StringBuilder manifest = new StringBuilder();
						manifest.append("Manifest-Version: 1.0\n");
						manifest.append("Bundle-ManifestVersion: 2\n");
						manifest.append("Bundle-Name: UI SAP Icons\n");
						manifest.append("Bundle-SymbolicName: org.vclipse.vcml.ui_sapicons\n");
						manifest.append("Bundle-Version: 0.3.0.qualifier\n");
						manifest.append("Bundle-Vendor: webXcerpt Software GmbH, Munich, Germany\n");
						manifest.append("Fragment-Host: org.vclipse.vcml.ui;bundle-version=\"0.3.0\"\n");
						manifest.append("Bundle-RequiredExecutionEnvironment: JavaSE-1.6\n");
						metaInfFolder.getFile("MANIFEST.MF").create(new StringInputStream(manifest.toString()), true, monitor);
						monitor.worked(1);

						monitor.subTask("Creating build.properties file");
						StringBuilder buildProperties = new StringBuilder();
						buildProperties.append("bin.includes = META-INF/,\\\n");
						buildProperties.append("                .,\\\n");
						buildProperties.append("                icons/");
						project.getFile("build.properties").create(new StringInputStream(buildProperties.toString()), true, monitor);
						monitor.worked(1);
						
						monitor.subTask("Exporting plug-in to the workspace");
						FeatureExportInfo info = new FeatureExportInfo();
						info.toDirectory = true;
						info.useJarFormat = true;
						info.exportSource = false;
						info.destinationDirectory = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
						info.items = new Object[]{PDECore.getDefault().getModelManager().findModel(project)};
						
						PluginExportOperation job = new PluginExportOperation(info, "");
						job.setUser(true);
						job.schedule();
						job.setProperty(IProgressConstants.ICON_PROPERTY, PDEPluginImages.DESC_PLUGIN_OBJ);
						monitor.worked(1);

						project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
						monitor.done();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private Map<String, byte[]> readArchiveFile(File file) throws IOException {
		String lowerCase = file.getName().toLowerCase();
		HashMap<String, byte[]> result = new HashMap<String, byte[]>();
		if(lowerCase.endsWith(".zip") || lowerCase.endsWith(".jar")) {
			byte[] buffer = new byte[32 * 1024];
			ZipFile zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while(entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				InputStream is = zipFile.getInputStream(entry);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				while(true) {
					int numRead = is.read(buffer, 0, buffer.length);
					if(numRead == -1) {
						break;
					}
					os.write(buffer, 0, numRead);
				}
				is.close();
				os.close();
				result.put(entry.getName(), os.toByteArray());
			}
			zipFile.close();
		}
		return result;
	}
}
