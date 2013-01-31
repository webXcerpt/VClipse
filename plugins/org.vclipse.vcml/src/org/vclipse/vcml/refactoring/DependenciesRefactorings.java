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
package org.vclipse.vcml.refactoring;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.vcml.vcml.BinaryCondition;
import org.vclipse.vcml.vcml.CharacteristicReference_C;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.ComparisonOperator;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.MDataCharacteristic_P;
import org.vclipse.vcml.vcml.NumberList;
import org.vclipse.vcml.vcml.NumberListEntry;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.PFunction;
import org.vclipse.vcml.vcml.SymbolList;
import org.vclipse.vcml.vcml.SymbolicLiteral;

import com.google.common.collect.Lists;

public class DependenciesRefactorings extends ConstraintRefactorings {

	/*
	 * Each value in the list is replaced through an equality expression
	 */
	public void refactoring_Extract_InCondition_C_list(IRefactoringContext context, InCondition_C condtion) {
		CharacteristicReference_C cstic = condtion.getCharacteristic();
		org.vclipse.vcml.vcml.List list = condtion.getList();
		List<Comparison> comparisons = Lists.newArrayList();
		if(list instanceof NumberList) {
			for(NumberListEntry entry : ((NumberList)list).getEntries()) {
				if(entry instanceof NumericLiteral) {
					Comparison comparison = VCML_FACTORY.createComparison();
					comparison.setLeft(EcoreUtil.copy(cstic));
					comparison.setOperator(ComparisonOperator.EQ);
					comparison.setRight(EcoreUtil.copy((NumericLiteral)entry));
					comparisons.add(comparison);
				}
			}
		} else if(list instanceof SymbolList) {
			EList<SymbolicLiteral> entries = ((SymbolList)list).getEntries();
			for(SymbolicLiteral literal : entries) {
				Comparison comparison = VCML_FACTORY.createComparison();
				comparison.setLeft(EcoreUtil.copy(cstic));
				comparison.setOperator(ComparisonOperator.EQ);
				comparison.setRight(EcoreUtil.copy(literal));
				comparisons.add(comparison);
			}
		}
		BinaryCondition be = VCML_FACTORY.createBinaryCondition();
		BinaryCondition current = be;
		for(Comparison comparison : comparisons) {
			if(be.getLeft() == null) {
				be.setLeft(comparison);
				be.setOperator("or");
				continue;
			} 
			if(comparisons.indexOf(comparison) != comparisons.size() - 1) {
				current = VCML_FACTORY.createBinaryCondition();
				current.setLeft(comparison);
				current.setOperator("or");
				be.setRight(current);
			} else {
				current.setRight(comparison);
			}
		}
		
		EObject container = condtion.eContainer();
		EReference feature = condtion.eContainmentFeature();
		container.eSet(feature, be);
		context.setSourceElement(container);
	}

	/*
	 * Replaces a pfunction value on the right side with a numeric or a symbolic literal
	 */
	public void refactoring_Replace_PFunction_values(IRefactoringContext context, PFunction function) {
		EStructuralFeature feature = context.getStructuralFeature();
		Object value = function.eGet(feature);
		int index = context.getIndex();

		if(value instanceof List<?>) {
			List<?> values = (List<?>)value;
			Object object = values.get(index);
			CharacteristicReference_P cstic_p = null;
			if(object instanceof MDataCharacteristic_P) {
				cstic_p = ((MDataCharacteristic_P)object).getCharacteristic();
			} else if(object instanceof CharacteristicReference_P) {
				cstic_p = (CharacteristicReference_P)object;
			} else if(object instanceof SymbolicLiteral) {
				// TODO implement
			}
			if(cstic_p != null) {
				Map<?, ?> args = context.getAttributes();
				Resource resource = cstic_p.eResource();
				Object button_state = args.get(BUTTON_STATE);
				if(button_state instanceof Boolean && (Boolean)button_state) {
					EObject element = context.getSourceElement();
					URI contextUri = element.eResource().getURI();
					String contextSegment = contextUri.lastSegment();
					for(IReferenceDescription description :  referencesFinder.getReferences(cstic_p.getCharacteristic())) {
						URI uri = description.getSourceEObjectUri();
						String fileName = uri.trimFileExtension().lastSegment();
						if(contextSegment.contains(fileName)) {
							String fragment = uri.fragment();
							if(fragment.contains("@values.")) {
								EObject eObject = resource.getEObject(fragment);
								EObject first = cstic_p.eContainer();
								EObject second = eObject.eContainer();
								if(first != null && second != null) {
									if(first.eClass() == second.eClass()) {
										if(eObject instanceof CharacteristicReference_P && eObject != cstic_p) {
											replaceCsticReference((CharacteristicReference_P)eObject, args);
										}
									}
								}
							}						
						}
					}
				} 
				replaceCsticReference(cstic_p, args);
			}
		}
	}
	
	protected void replaceCsticReference(CharacteristicReference_P cstic_p, Map<?, ?> args) {
		PFunction pfunction = EcoreUtil2.getContainerOfType(cstic_p, PFunction.class);
		if(pfunction != null) {
			Object text_field_entry = args.get(TEXT_FIELD_ENTRY);
			if(text_field_entry instanceof String) {
				String text = (String)text_field_entry;
				Literal newLiteral = null;
				try {
					Integer.parseInt(text);
					NumericLiteral numLit = VCML_FACTORY.createNumericLiteral();
					numLit.setValue(text);
					newLiteral = numLit;
				} catch(Exception exception) {
					SymbolicLiteral symlit = VCML_FACTORY.createSymbolicLiteral();
					symlit.setValue(text);
					newLiteral = symlit;
				}
				
				EList<Literal> values = pfunction.getValues();
				values.add(newLiteral);
				values.remove(cstic_p);
				
				EObject removeObject = cstic_p.eContainer() instanceof MDataCharacteristic_P ? cstic_p.eContainer() : cstic_p;
				values.remove(removeObject);
			}			
		}
	}
}
