/** 
 * Copyright (c) 2010 - 2015 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.base.ui.util

import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.ui.IEditorPart
import org.eclipse.ui.IEditorSite
import org.eclipse.ui.IWorkbenchPartSite
import org.eclipse.xtext.parser.IParseResult
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.ui.editor.XtextEditor
import org.eclipse.xtext.ui.editor.utils.EditorUtils
import org.eclipse.xtext.util.concurrent.IUnitOfWork

/** 
 * Utility for working with editors.
 */
class EditorUtilsExtensions {
	/** 
	 * Try to get an xtext resource for an editor part, returns null if it goes wrong.
	 */
	def static XtextResource getXtextResource(IEditorPart editorPart) {
		if (editorPart instanceof XtextEditor) {
			val EObject rootElement = getRootElement(editorPart)
			if (rootElement !== null) {
				val Resource resource = rootElement.eResource
				if (resource instanceof XtextResource)
					return resource
			}
		}
		return null
	}

	/** 
	 * Returns the root element for a xtext editor, null if the document is not parsed 
	 * or editor is not ready.
	 */
	def static EObject getRootElement(XtextEditor editor) {
		if (editor === null) {
			return null
		}
		return editor.document.readOnly(([ XtextResource resource |
			val IParseResult parseResult = resource.parseResult
			return parseResult?.getRootASTElement()
		] as IUnitOfWork<EObject, XtextResource>))
	}

	/** 
	 * Returns an instance of type {@link IProgressMonitor} for an active xtext editor,
	 * of type {@link NullProgressMonitor} otherwise.
	 */
	def static IProgressMonitor getProgressMonitor() {
		var XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor()
		if (xtextEditor !== null) {
			val IWorkbenchPartSite site = xtextEditor.site
			if (site instanceof IEditorSite)
				return site.actionBars.statusLineManager.progressMonitor
		}
		return new NullProgressMonitor
	}

}
