package org.vclipse.refactoring.core;

import org.eclipse.emf.ecore.EObject;

public interface IChangeCompare {

	public EObject getCurrent();
	
	public EObject getChanged();
}
