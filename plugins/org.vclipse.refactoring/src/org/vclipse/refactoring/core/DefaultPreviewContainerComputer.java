/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.refactoring.core;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.vclipse.refactoring.IPreviewObjectComputer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DefaultPreviewContainerComputer implements IPreviewObjectComputer {

	@Override
	public Set<EClass> getFavoredTypes() {
		return Sets.newHashSet();
	}

	@Override
	public Set<EClass> getIgnoreTypes() {
		return Sets.newHashSet();
	}

	@Override
	public List<EObject> getExisting(EObject existing, EObject refactored, FeatureChange featureChange) {
		return Lists.newArrayList(existing);
	}

	@Override
	public List<EObject> getRefactored(EObject existing, EObject refactored, FeatureChange featureChange) {
		return Lists.newArrayList(refactored);
	}
}
