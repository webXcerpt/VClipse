package org.vclipse.configscan;

import org.eclipse.emf.ecore.EObject;
import org.vclipse.configscan.impl.DefaultTestObjectFilter;

import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultTestObjectFilter.class)
public interface ITestObjectFilter {

	// returns false if object should not be handled, true otherwise
	public boolean passesFilter(EObject object);
	
}
