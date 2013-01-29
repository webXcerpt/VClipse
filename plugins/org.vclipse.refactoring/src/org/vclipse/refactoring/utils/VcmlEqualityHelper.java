/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 *     www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.refactoring.utils;

import org.eclipse.emf.compare.CompareFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.utils.IEqualityHelper;

/**
 * 
 */
public class VcmlEqualityHelper extends DefaultEqualityHelperFactory {

	@Override
	public IEqualityHelper createEqualityHelper() {
		IEqualityHelper equalityHelper = super.createEqualityHelper();
		equalityHelper.setTarget(CompareFactory.eINSTANCE.createComparison());
		return equalityHelper;
	}
}
