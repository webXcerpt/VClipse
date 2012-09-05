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

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.inject.ImplementedBy;

@ImplementedBy(RefactoringContext.class)
public interface IRefactoringContext {
	
	public void setSourceElement(EObject object);
	
	public EObject getSourceElement();
	
	public void setStructuralFeature(EStructuralFeature feature);
	
	public EStructuralFeature getStructuralFeature();
	
	public void setType(RefactoringType type);
	
	public RefactoringType getType();
	
	public void addAttribute(Object key, Object value);

	public Map<?, ?> getAttributes();
	
	public String getDescription();
	
	public String getText();
	
	public void setText(String text);
}
