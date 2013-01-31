/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
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
