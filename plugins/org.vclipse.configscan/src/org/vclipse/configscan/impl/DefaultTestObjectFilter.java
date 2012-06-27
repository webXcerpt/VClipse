package org.vclipse.configscan.impl;

import org.eclipse.emf.ecore.EObject;
import org.vclipse.configscan.ITestObjectFilter;

public class DefaultTestObjectFilter implements ITestObjectFilter {

	@Override
	public boolean passesFilter(EObject object) {
		// all object should be handled
		return true;
	}
}
