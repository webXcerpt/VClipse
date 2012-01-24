package org.vclipse.configscan.impl;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.vclipse.configscan.ITestObjectFilter;
import org.vclipse.configscan.impl.TestCaseAdapterFactory.TestCaseAdapter;
import org.vclipse.configscan.impl.model.TestCase.Status;

public class FailureTestObjectFilter implements ITestObjectFilter {

	@Override
	public boolean passesFilter(EObject object) {
		TestCaseAdapter adapter = (TestCaseAdapter)EcoreUtil.getAdapter(object.eAdapters(), EObject.class);
		if(adapter != null) {
			boolean passes = Status.FAILURE == adapter.getTestCase().getStatus();
			removeAdapter(object.eAdapters(), EObject.class);
			return passes;
		}
		return false;
	}
	
	protected void removeAdapter(List<Adapter> adapters, Object type) {
		Adapter[] adapterListCopy = (Adapter[])Arrays.copyOf(adapters.toArray(), adapters.size());
		adapters.clear();
		for(Adapter adapter : adapterListCopy) {
			if(!adapter.isAdapterForType(type)) {
				adapters.add(adapter);
			}
		}
	}
}
