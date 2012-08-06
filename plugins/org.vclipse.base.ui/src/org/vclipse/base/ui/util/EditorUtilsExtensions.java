package org.vclipse.base.ui.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
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
}
