/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator and others
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan.vcmlt.ui.action;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.eclipse.xtext.util.Files;
import org.w3c.dom.Document;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.vcmlt.vcmlT.Model;

public class ConvertConfigScanTestLog2VcmlTAction implements IObjectActionDelegate {

	@Inject
	private ConfigScanTestLog2VcmlT conversion;
	
	@Inject
	private DocumentUtility documentUtility;
	
	private Resource cml2Resource;
	
	private Document document;
	
	private IContainer preselectedContainer;
	
	@Override
	public void run(IAction action) {
		if(document != null && preselectedContainer != null) {
			FilteredResourcesSelectionDialog resourceDialog = 
					new FilteredResourcesSelectionDialog(Display.getDefault().getActiveShell(), false, 
							ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
			resourceDialog.setTitle("Choose VCML file");
			if(Dialog.OK == resourceDialog.open()) {
				Object result = resourceDialog.getFirstResult();
				if(result instanceof IFile) {
					IFile file = (IFile)result;
					if(file.getFileExtension().equals("vcml")) {
						XtextResourceSet xtextResourceSet = new XtextResourceSet();
						cml2Resource = xtextResourceSet.getResource(URI.createFileURI(file.getFullPath().toString()), true);
						Model model = conversion.convert(document, cml2Resource);
						Resource vcmltResource = xtextResourceSet.createResource(cml2Resource.getURI().appendFileExtension("vcmlt"));
						EList<EObject> contents = vcmltResource.getContents();
						if(!contents.isEmpty()) {
							contents.clear();
						}
						contents.add(model);
						try {
							vcmltResource.save(Maps.newHashMap());
							ResourceUtil.getFile(vcmltResource).refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
						} catch (IOException e) {
							e.printStackTrace();
						} catch (CoreException e) {
							e.printStackTrace();
						}
					} else {
						cml2Resource = null;
					}
				}
			}
		}
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		Iterator<?> iterator = ((IStructuredSelection)selection).iterator();
		while(iterator.hasNext()) {
			Object object = iterator.next();
			if(object instanceof IFile) {
				try {
					IFile file = (IFile)object;
					document = documentUtility.parse(Files.readStreamIntoString(file.getContents()));
					preselectedContainer = file.getParent();
				 } catch (CoreException e) {
					 document = null;
					 preselectedContainer = null;
					 e.printStackTrace();
				 }
			}
		}
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}
}
