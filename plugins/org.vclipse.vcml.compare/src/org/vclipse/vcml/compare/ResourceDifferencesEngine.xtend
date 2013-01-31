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
import org.eclipse.emf.common.util.Monitor
import org.eclipse.emf.compare.Match
import org.eclipse.emf.compare.diff.DefaultDiffEngine
import org.vclipse.vcml.vcml.Dependency
import org.eclipse.emf.compare.DifferenceKind
import org.eclipse.emf.compare.DifferenceSource

@Singleton
class ResourceDifferencesEngine extends DefaultDiffEngine {
	
	private FeatureFilter featureFilter
	
	@Inject
	new(ResourceChangesProcessor processor, FeatureFilter featureFilter) {
		super(processor)
		this.featureFilter = featureFilter
	}
	
	override protected createFeatureFilter() {
		return featureFilter
	}
	
	override protected checkForDifferences(Match match, Monitor monitor) {
		val left = match.left
		val right = match.right
		if(left instanceof Dependency && right instanceof Dependency) {
			val resourceProcessor = (diffProcessor as ResourceChangesProcessor)
			resourceProcessor.dependencyChange(left as Dependency, right as Dependency, DifferenceKind::ADD, DifferenceSource::LEFT)
			return
		}
		super.checkForDifferences(match, monitor)
	}	
}