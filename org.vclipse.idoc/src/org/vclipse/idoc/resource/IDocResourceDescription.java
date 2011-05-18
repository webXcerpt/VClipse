package org.vclipse.idoc.resource;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescription;

public class IDocResourceDescription extends DefaultResourceDescription {

	public IDocResourceDescription(final Resource resource,
			final IQualifiedNameProvider nameProvider) {
		super(resource, nameProvider);
	}

	@Override
	protected List<IEObjectDescription> computeExportedObjects() {
		return Collections.emptyList();
	}

}
