package org.vclipse.base.ui.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

public class EditorUtilsExtensions {

	public static XtextResource getResource(IEditorPart editorPart) {
		if(editorPart instanceof XtextEditor) {
			XtextEditor xtextEditor = (XtextEditor)editorPart;
			return (XtextResource)xtextEditor.getDocument().readOnly(new IUnitOfWork<EObject, XtextResource>(){
				@Override
				public EObject exec(XtextResource resource) throws Exception {
					return resource.getParseResult().getRootASTElement();
				}
			}).eResource();
		}
		return null;
	}
	
	public static EObject getRootElement(XtextEditor editor) {
		if(editor != null) {
			EObject parseResult = editor.getDocument().readOnly(
				new IUnitOfWork<EObject, XtextResource>() {
					public EObject exec(XtextResource resource) throws Exception {
						return resource.getParseResult().getRootASTElement();
					}
				});
			return parseResult;
		}
		return null;
	}
	
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
