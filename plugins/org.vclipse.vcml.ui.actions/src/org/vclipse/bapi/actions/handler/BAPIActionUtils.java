package org.vclipse.bapi.actions.handler;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.vclipse.vcml.vcml.VCObject;

public class BAPIActionUtils {

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
	
	public static VCObject getVCObject(EObjectAtOffsetHelper offsetHelper, ITextSelection textSelection, XtextResource resource) {
		int offset = textSelection.getOffset();
		EObject elementAt = offsetHelper.resolveContainedElementAt(resource, offset);
		if(elementAt == null) {
			elementAt = offsetHelper.resolveElementAt(resource, offset);			
		}
		if(elementAt instanceof VCObject) {
			return (VCObject)elementAt;
		}
		return null;
	}
	
	public static Class<?> getInstanceType(EObject object) throws ClassNotFoundException {
		return Class.forName(getInstanceTypeName(object));
	}
	
	public static String getInstanceTypeName(EObject object) {
		return object.eClass().getInstanceTypeName();
	}
}
