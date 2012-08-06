package org.vclipse.bapi.actions.handler;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.vclipse.vcml.vcml.VCObject;

public class BAPIActionUtils {

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
	
	public static Class<?> getInstanceType(Object object) throws ClassNotFoundException {
		return Class.forName(getInstanceTypeName(object));
	}
	
	public static String getInstanceTypeName(Object object) {
		if(object instanceof EObject) {
			return ((EObject)object).eClass().getInstanceTypeName();			
		} else {
			Class<?>[] interfaces = object.getClass().getInterfaces();
			if(interfaces.length > 0) {
				return interfaces[0].getName();				
			}
		}
		return "";
	}
}
