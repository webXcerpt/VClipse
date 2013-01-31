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
package org.vclipse.refactoring;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.change.FeatureChange;

public interface IPreviewObjectComputer {

	public Set<EClass> getFavoredTypes();
	
	public Set<EClass> getIgnoreTypes();
	
	public List<EObject> getExisting(EObject existing, EObject refactored, FeatureChange featureChange);
	
	public List<EObject> getRefactored(EObject existing, EObject refactored, FeatureChange featureChange);
	
}
