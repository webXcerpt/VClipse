package org.vclipse.vcml.diffmerge;

import java.util.regex.Pattern;

import org.eclipse.emf.diffmerge.api.scopes.IModelScope;
import org.eclipse.emf.diffmerge.impl.policies.DefaultMatchPolicy;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.vclipse.vcml.vcml.CharacteristicGroup;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

public class VcmlMatchPolicy extends DefaultMatchPolicy {

	@Override
	public Comparable<?> getMatchId(EObject element_p, IModelScope scope_p) {
		if (element_p instanceof VCObject) {
			VCObject vcObject = (VCObject)element_p;
			return vcObject.eClass().getName() + "/" + vcObject.getName();
		}
		if (element_p instanceof VcmlModel) {
			return element_p.eClass().getName();
		}
		if (element_p instanceof CharacteristicGroup) {
			return getMatchId(element_p.eContainer(), scope_p) + "/CharacteristicGroup/" + ((CharacteristicGroup)element_p).getName();
		}
		
		VCObject containerObject = EcoreUtil2.getContainerOfType(element_p, VCObject.class);
		if (containerObject!=null) {
			return getMatchId(containerObject, scope_p) + getVcmlUri(element_p);
		}

		// return getMatchId(element_p.eContainer(), scope_p) + "/" + element_p.eContainingFeature().getName();
		// System.err.println(element_p + "\t" + getVcmlUri(element_p) + "\t" + getAttributeId(element_p) + "\t" + getName(element_p) + "\t" + getUriFragment(element_p) + "\t" + getXmlId(element_p));
		
		return super.getMatchId(element_p, scope_p);
	}

	static Pattern FRAGMENT_OBJECTS_PATTERN = Pattern.compile("//@objects\\.\\d+");
	
	private String getVcmlUri(EObject obj) {
		String fragment = EcoreUtil.getURI(obj).fragment();
		String result = FRAGMENT_OBJECTS_PATTERN.matcher(fragment).replaceFirst("");
		return result;
	}
	
}
