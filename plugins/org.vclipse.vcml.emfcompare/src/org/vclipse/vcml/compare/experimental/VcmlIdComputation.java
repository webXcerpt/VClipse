package org.vclipse.vcml.compare.experimental;

import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.vclipse.vcml.vcml.CharacteristicGroup;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.common.base.Function;

public class VcmlIdComputation implements Function<EObject, String>{

	@Override
	public String apply(EObject element_p) {
		if (element_p instanceof VCObject) {
			VCObject vcObject = (VCObject)element_p;
			return vcObject.eClass().getName() + "/" + vcObject.getName();
		}
		if (element_p instanceof VcmlModel) {
			return element_p.eClass().getName();
		}
		return null;
//		if (element_p instanceof CharacteristicGroup) {
//			return apply(element_p.eContainer()) + "/CharacteristicGroup/" + ((CharacteristicGroup)element_p).getName();
//		}
//		
//		VCObject containerObject = EcoreUtil2.getContainerOfType(element_p, VCObject.class);
//		if (containerObject!=null) {
//			return apply(containerObject) + getVcmlUri(element_p);
//		}
//
//		return getVcmlUri(element_p); // case for import and options
	}

	static Pattern FRAGMENT_OBJECTS_PATTERN = Pattern.compile("//@objects\\.\\d+");
	
	private String getVcmlUri(EObject obj) {
		String fragment = EcoreUtil.getURI(obj).fragment();
		String result = FRAGMENT_OBJECTS_PATTERN.matcher(fragment).replaceFirst("");
		return result;
	}
	
}
