package org.vclipse.configscan.vcmlt.naming;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.vclipse.base.naming.INameProvider;

import com.google.inject.Inject;

public class SapNameProvider extends INameProvider.AbstractImpl {

	@Inject
	IQualifiedNameProvider qualifiedNameProvider;
	
	@Override
	public String getName(final EObject obj) {
		return qualifiedNameProvider.getFullyQualifiedName(obj).getLastSegment();
	}

}
