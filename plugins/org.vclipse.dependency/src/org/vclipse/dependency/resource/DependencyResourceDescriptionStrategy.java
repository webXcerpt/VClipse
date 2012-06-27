package org.vclipse.dependency.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;

public class DependencyResourceDescriptionStrategy extends DefaultResourceDescriptionStrategy {

	// dependency source resources do not export any objects
	@Override
	public boolean createEObjectDescriptions(EObject eObject, IAcceptor<IEObjectDescription> acceptor) {
		return false;
	}
	
}
