package org.vclipse.configscan.impl;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreAdapterFactory;
import org.vclipse.configscan.impl.model.TestCase;

public class TestCaseAdapterFactory extends EcoreAdapterFactory {

	public static final TestCaseAdapterFactory INSTANCE = new TestCaseAdapterFactory();
	
	@Override
	public Adapter createAdapter(Notifier target) {
		return new TestCaseAdapter();
	}
	
	@Override
	public boolean isFactoryForType(Object type) {
		return type == TestCase.class;
	}
	
	public class TestCaseAdapter extends AdapterImpl {

		private TestCase testCase;
		
		@Override
		public boolean isAdapterForType(Object type) {
			return type == EObject.class;
		}
	
		public void setTestCase(TestCase testCase) {
			this.testCase = testCase;
		}
		
		public TestCase getTestCase() {
			return testCase;
		}
	}
}
