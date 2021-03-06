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
package org.vclipse.constraint.ui.outline;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.impl.DocumentRootNode;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.editor.outline.impl.EStructuralFeatureNode;
import org.vclipse.vcml.vcml.BinaryCondition;
import org.vclipse.vcml.vcml.CharacteristicReference_C;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.Condition;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintClass;
import org.vclipse.vcml.vcml.ConstraintMaterial;
import org.vclipse.vcml.vcml.ConstraintObject;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintRestrictionFalse;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.PartOfCondition;
import org.vclipse.vcml.vcml.VcmlPackage;

/**
 * customization of the default outline structure
 * 
 */
public class ConstraintOutlineTreeProvider extends DefaultOutlineTreeProvider {

	private static final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;

	Object text(EObject object) {
		return object == null ? null : textDispatcher.invoke(object);
	}
	
	void _createChildren(DocumentRootNode parentNode, ConstraintSource constraintSource) {
		EList<ConstraintObject> objects = constraintSource.getObjects();
		if(!objects.isEmpty()) {
			EStructuralFeatureNode objectsNode = createEStructuralFeatureNode(parentNode, constraintSource, 
					VCML_PACKAGE.getConstraintSource_Objects(), _image(constraintSource), "objects", false);
			for(ConstraintObject constraintObject : objects) {
				createNode(objectsNode, constraintObject);
			}			
		}
		
		Condition condition = constraintSource.getCondition();
		if(condition != null) {
			EStructuralFeatureNode objectsNode = createEStructuralFeatureNode(parentNode, constraintSource, 
					VCML_PACKAGE.getConstraintSource_Condition(), _image(constraintSource), "condition", false);
			createNode(objectsNode, constraintSource.getCondition());			
		}
		
		EList<ConstraintRestriction> restrictions = constraintSource.getRestrictions();
		if(!restrictions.isEmpty()) {
			EStructuralFeatureNode restrictionsNode = createEStructuralFeatureNode(parentNode, constraintSource, 
					VCML_PACKAGE.getConstraintSource_Restrictions(), _image(constraintSource), "restrictions", false);
			
			for(ConstraintRestriction constraintRestriction : restrictions) {
				createNode(restrictionsNode, constraintRestriction);
			}			
		}
		
		EList<CharacteristicReference_C> inferences = constraintSource.getInferences();
		if(!inferences.isEmpty()) {
			EStructuralFeatureNode inferencesNode = createEStructuralFeatureNode(parentNode, constraintSource, 
					VCML_PACKAGE.getConstraintSource_Inferences(), _image(constraintSource), "inferences", false);
			for(CharacteristicReference_C reference : inferences) {
				createNode(inferencesNode, reference);
			}
		}
	}
	
	Object _text(ConstraintClass klass) {
		return super._text(klass);
	}
	
	Object _text(ConstraintMaterial material) {
		return super._text(material);
	}
	
	Object _text(ConditionalConstraintRestriction restriction) {
		return super._text(restriction);
	}
	
	void _createNode(IOutlineNode parentNode, ConditionalConstraintRestriction restriction) {
		if(restriction != null) {
			createNode(parentNode, restriction.getRestriction());
			createNode(parentNode, restriction.getCondition());			
		}
	}
	
	boolean _isLeaf(ConditionalConstraintRestriction restriction) {
		return restriction.eContainer() instanceof Condition;
	}
	
	boolean _isLeaf(Comparison comparison) {
		return true;
	}
	
	Object _text(Comparison comparison) {
		return text(comparison.getLeft()) + " " + comparison.getOperator().getLiteral() + " " + text(comparison.getRight());
	}
	
	boolean _isLeaf(BinaryCondition condition) {
		return true;
	}
	
	Object _text(BinaryCondition condition) {
		return text(condition.getLeft()) + " " + condition.getOperator() + " " + text(condition.getRight()); 
	}
	
	Object _text(ConstraintRestrictionFalse restriction) {
		return "false";
	}
	
	Object _text(PartOfCondition partOfCondition) {
		return "part_of(" + partOfCondition.getChild().getName() + ", " + partOfCondition.getParent().getName() + ")";
	}
	
	@Override
	protected EObjectNode createEObjectNode(IOutlineNode parentNode, EObject modelElement, Image image, Object text, boolean isLeaf) {
		EObjectNode node = super.createEObjectNode(parentNode, modelElement, image, text, isLeaf);
		node.setShortTextRegion(locationInFileProvider.getFullTextRegion(modelElement));
		return node;
	}

	@Override
	protected EStructuralFeatureNode createEStructuralFeatureNode(IOutlineNode parentNode, EObject owner, EStructuralFeature feature, Image image, Object text, boolean isLeaf) {
		EStructuralFeatureNode node = super.createEStructuralFeatureNode(parentNode, owner, feature, image, text, isLeaf);
		node.setTextRegion(locationInFileProvider.getFullTextRegion(owner, feature, 0));
		return node;
	}
}