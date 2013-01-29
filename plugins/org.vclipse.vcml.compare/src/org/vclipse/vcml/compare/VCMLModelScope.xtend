/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *		webXcerpt Software GmbH - initial creator
 *		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.vcml.compare

import com.google.common.base.Predicates
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.compare.scope.FilterComparisonScope
import org.vclipse.vcml.vcml.Option
import org.vclipse.vcml.vcml.VCObject

/**
 * This scope contains VCObject and Option objects.
 */
class VCMLModelScope extends FilterComparisonScope {
	
	/**
	 * Constructor
	 */
	new(Notifier left, Notifier right) {
		super(left, right, null)
		setEObjectContentFilter(
			Predicates::or(
				Predicates::instanceOf(typeof(Option)), 
				Predicates::instanceOf(typeof(VCObject))
			)
		)
	}
}