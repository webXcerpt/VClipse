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
package org.vclipse.vcml.compare

import com.google.inject.Inject
import com.google.inject.Singleton
import org.eclipse.emf.compare.diff.DefaultDiffEngine

@Singleton
class ModelDifferencesEngine extends DefaultDiffEngine {
	
	private FeatureFilter featureFilter
	
	@Inject
	new(ModelChangesProcessor processor, FeatureFilter featureFilter) {
		super(processor)
		this.featureFilter = featureFilter
	}
	
	override protected createFeatureFilter() {
		return featureFilter
	}
}