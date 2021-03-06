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
package org.vclipse.configscan.views;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;

public class DoubleClickListener implements IDoubleClickListener {

	@Override
	public void doubleClick(final DoubleClickEvent event) {
		TreeViewer viewer = (TreeViewer)event.getViewer();
		IContentProvider contentProvider = viewer.getContentProvider();
		if(contentProvider instanceof ContentProvider) {
			Object firstElement = ((IStructuredSelection)event.getSelection()).getFirstElement();
			if(firstElement instanceof TestCase) {
				select((TestCase)firstElement);
			}
			return;
		} 
		if(contentProvider instanceof ErrorBasedContentProvider) {
			TreeItem[] items = viewer.getTree().getSelection();
			if(items.length > 0) {
				TreeItem treeItem = items[0];
				Object treeItemData = treeItem.getData();
				if(treeItemData instanceof TestGroup) {
					select((TestGroup)treeItemData);
				} else if(treeItemData instanceof TestCase) {
					select((TestCase)treeItemData);
				}
			}
		} 
	}
	
	protected void select(TestCase testCase) {
		URI testStatementUri = testCase.getSourceURI();
		if(testStatementUri != null) {
			Resource resource = new XtextResourceSet().getResource(testStatementUri, true);
			if(resource == null) {
				ConfigScanPlugin.log("Can not create resource for uri " + testStatementUri.toString(), IStatus.ERROR);
			}
			EObject associatedEObject = resource.getResourceSet().getEObject(testStatementUri, true);
			if(associatedEObject != null) {
				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					Resource eResource = associatedEObject.eResource();
					IEditorPart openEditor = IDE.openEditor(activePage, ResourceUtil.getFile(eResource), true);
					if(openEditor instanceof XtextEditor) {
						INode node = NodeModelUtils.getNode(associatedEObject);
						((XtextEditor)openEditor).selectAndReveal(node.getOffset(), node.getLength());
					}
				} catch (PartInitException exception) {
					ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
				}
			}
		} else {
			System.err.println("Uri for " + testCase.getTitle() + " is null.");
		}
	}
}
