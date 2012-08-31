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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public interface IEvaluationResult {

	public void setObject(EObject object);
	
	public EObject getObject();
	
	public void setFeature(EStructuralFeature feature);
	
	public EStructuralFeature getStructuralFeature();
	
	public void success(boolean success);
	
	public boolean success();
}
