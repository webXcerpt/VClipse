/*******************************************************************************
 * Copyright (c) 2008 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.base.ui.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

/**
 * Utility for working with editors.
 */
public class EditorUtilsExtensions {

	/**
	 * Try to get an xtext resource for an editor part, returns null if it goes wrong.
	 */
	public static XtextResource getXtextResource(IEditorPart editorPart) {
		if(editorPart instanceof XtextEditor) {
			EObject rootElement = getRootElement((XtextEditor)editorPart);
			if(rootElement != null) {
				Resource resource = rootElement.eResource();
				if(resource instanceof XtextResource) {
					return (XtextResource)resource;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the root element for a xtext editor, null if the document is not parsed 
	 * or editor is not ready.
	 */
	public static EObject getRootElement(XtextEditor editor) {
		if(editor == null) {
			return null;
		}
		return editor.getDocument().readOnly(new IUnitOfWork<EObject, XtextResource>() {
			public EObject exec(XtextResource resource) throws Exception {
				IParseResult parseResult = resource.getParseResult();
				return parseResult == null ? null : parseResult.getRootASTElement();
			}
		});
	}
	
	/**
	 * Returns an instance of type {@link IProgressMonitor} for an active xtext editor,
	 * of type {@link NullProgressMonitor} otherwise.
	 */
	public static IProgressMonitor getProgressMonitor() {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
		if(xtextEditor != null) {
			IWorkbenchPartSite site = xtextEditor.getSite();
			if(site instanceof IEditorSite) {
				IActionBars actionBars = ((IEditorSite)site).getActionBars();
				return actionBars.getStatusLineManager().getProgressMonitor();
			}
		}
		return new NullProgressMonitor();
	}
}
