package org.vclipse.vcml.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.VCObject;

public class VCMLResourceDescriptionStrategy extends DefaultResourceDescriptionStrategy {

	@Override
	public boolean createEObjectDescriptions(EObject eObject, IAcceptor<IEObjectDescription> acceptor) {
		if (eObject instanceof VCObject || eObject instanceof Model) {
			return super.createEObjectDescriptions(eObject, acceptor);
		} else {
			return false; // prune all children of VCObjects
		}
	}
	
}
