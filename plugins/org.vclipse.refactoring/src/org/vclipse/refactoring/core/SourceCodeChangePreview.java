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

import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.vclipse.base.ui.compare.EObjectTypedElement;
import org.vclipse.refactoring.IPreviewProvider;
import org.vclipse.refactoring.ui.RefactoringUtility;

public class SourceCodeChangePreview extends SourceCodeChange implements IPreviewProvider {

	private DiffNode diffNode;
	
	public SourceCodeChangePreview(RefactoringUtility utility, EObject original, EObject refactoring, EStructuralFeature feature, Object newValue) {
		super(utility, original, refactoring, feature, newValue);
	}

	public DiffNode getPreview() {
		initializeValidationData(null);
		
		diffNode = new DiffNode(Differencer.CHANGE);
		
		EObjectTypedElement left = 
				originalObject == null ? 
						EObjectTypedElement.getEmpty() : new EObjectTypedElement(originalObject, serializer, nameProvider);
		
		diffNode.setLeft(left);
		
		EObjectTypedElement right =  
				new EObjectTypedElement(refactoredObject, serializer, nameProvider);
		
		diffNode.setRight(right);
		return diffNode;
	}
}
