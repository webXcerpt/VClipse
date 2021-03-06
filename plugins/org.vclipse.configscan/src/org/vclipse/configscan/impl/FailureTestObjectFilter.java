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
			List<Adapter> adapters = object.eAdapters();
			Adapter[] adapterListCopy = (Adapter[])Arrays.copyOf(adapters.toArray(), adapters.size());
			adapters.clear();
			for(Adapter adapter1 : adapterListCopy) {
				if(!adapter1.isAdapterForType(EObject.class)) {
					adapters.add(adapter1);
				}
			}
			return passes;
		}
		return true;
	}
}
