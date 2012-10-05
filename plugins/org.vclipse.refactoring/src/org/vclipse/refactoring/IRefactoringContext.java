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
package org.vclipse.refactoring;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.vclipse.refactoring.core.RefactoringContext;
import org.vclipse.refactoring.core.RefactoringType;

import com.google.inject.ImplementedBy;

@ImplementedBy(RefactoringContext.class)
public interface IRefactoringContext {
	
	public void setSourceElement(EObject object);
	
	public void setStructuralFeature(EStructuralFeature feature);
	
	public void setIndex(int index);
	
	public void setType(RefactoringType type);
	
	public void addAttribute(Object key, Object value);
	
	public void setLabel(String text);
	
	public EObject getSourceElement();
	
	public EStructuralFeature getStructuralFeature();
	
	public int getIndex();
	
	public RefactoringType getType();
	
	public Map<?, ?> getAttributes();
	
	public String getDescription();
	
	public String getLabel();

}
