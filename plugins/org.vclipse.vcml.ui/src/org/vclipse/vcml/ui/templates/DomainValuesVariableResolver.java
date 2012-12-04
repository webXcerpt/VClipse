/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.ui.templates;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.xtext.ui.editor.templates.AbstractTemplateVariableResolver;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContext;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicReference_C;
import org.vclipse.vcml.vcml.CharacteristicType;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.NumberListEntry;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericInterval;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.ObjectCharacteristicReference;
import org.vclipse.vcml.vcml.ShortVarReference;
import org.vclipse.vcml.vcml.SymbolicType;

import com.google.common.collect.Lists;

public class DomainValuesVariableResolver extends AbstractTemplateVariableResolver {
	public DomainValuesVariableResolver() {
		super("DomainValues", "Values of a characteristic's domain");
	}
	@Override
	public List<String> resolveValues(TemplateVariable variable, XtextTemplateContext xtextTemplateContext) {
		EObject currentModel = xtextTemplateContext.getContentAssistContext().getCurrentModel();
		if (currentModel instanceof InCondition_C) {
			CharacteristicReference_C ref = ((InCondition_C)currentModel).getCharacteristic();
			if (ref!=null && !ref.eIsProxy()) {
				return Lists.newArrayList(getValues(getCharacteristic(ref)));
			}
		}
		
		
		return Lists.newArrayList();
	}

	private Characteristic getCharacteristic(CharacteristicReference_C csticRef) {
		if (csticRef instanceof ObjectCharacteristicReference) {
			ObjectCharacteristicReference ocr = (ObjectCharacteristicReference)csticRef;
			return ocr.getCharacteristic();
		} else if (csticRef instanceof ShortVarReference) {
			ShortVarReference svr = (ShortVarReference)csticRef;
			return svr.getRef().getCharacteristic();
		}
		throw new IllegalArgumentException("unknown CharacteristicReference_C: " + csticRef);
	}
	
	private String getValues(Characteristic cstic) {
		StringBuffer result = new StringBuffer();
		CharacteristicType characteristicType = cstic.getType();
		if (characteristicType instanceof SymbolicType) {
			for (CharacteristicValue value : ((SymbolicType)characteristicType).getValues()) {
				if (result.length() > 0) {
					result.append(", ");
				}
				result.append("'").append(value.getName()).append("'");
			}
		} else if (characteristicType instanceof NumericType) {
			for (NumericCharacteristicValue value : ((NumericType)characteristicType).getValues()) {
				if (result.length() > 0) {
					result.append(", ");
				}
				NumberListEntry entry = value.getEntry();
				if (entry instanceof NumericLiteral) {
					result.append(((NumericLiteral)entry).getValue());
				} else if (entry instanceof NumericInterval) {
					NumericInterval interval = (NumericInterval)entry;
					result.append(interval.getLowerBound()).append("-").append(interval.getUpperBound());
				}
			}
		}
		return "(" + result + ")";
	}
	

}
