package org.vclipse.vcml.diffmerge;

import org.eclipse.emf.diffmerge.api.scopes.IModelScope;
import org.eclipse.emf.diffmerge.impl.policies.DefaultMatchPolicy;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.vcml.vcml.VCObject;

public class VcmlMatchPolicy extends DefaultMatchPolicy {

	@Override
	public Comparable<?> getMatchId(EObject element_p, IModelScope scope_p) {
		if (element_p instanceof VCObject) {
			VCObject vcObject = (VCObject)element_p;
			return vcObject.eClass().getName() + "/" + vcObject.getName();
		}
		return super.getMatchId(element_p, scope_p);
	}

}
