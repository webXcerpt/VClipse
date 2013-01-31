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
package org.vclipse.constraint.validation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.vclipse.constraint.validation.AbstractConstraintJavaValidator;
import org.vclipse.vcml.utils.ConstraintRestrictionExtensions;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicReference_C;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.ObjectCharacteristicReference;
import org.vclipse.vcml.vcml.ShortVarReference;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
 

public class ConstraintJavaValidator extends AbstractConstraintJavaValidator {
	
	@Inject 
	private ConstraintRestrictionExtensions expressionExtensions;
	
	@Check
	public void checkConstraintSource(ConstraintSource source) {
		checkSource(source);
	}
	 
	@Check(CheckType.FAST)
	public void checkConstraint(ConstraintSource source) {
		if(source!=null) {
			List<ConstraintRestriction> restrictions = source.getRestrictions();
			int size = Iterables.size(Iterables.filter(restrictions, ConditionalConstraintRestriction.class));
			if(size > 0 && restrictions.size() > size) {
				error("Mix of conditional and unconditional restrictions is not allowed in a constraint", VcmlPackage.Literals.CONSTRAINT_SOURCE__RESTRICTIONS);
				// TODO possible quickfix: split this constraint
			}
		}
	}
	
	@Check(CheckType.FAST)
	public void checkNotRestrictedInferences(ConstraintSource source) {
		// Collect the existing characteristics in the inferences part
		Map<Characteristic, CharacteristicReference_C> cstics2Reference = Maps.newHashMap();
		for(CharacteristicReference_C inference : source.getInferences()) {
			for(Characteristic cstic : expressionExtensions.getUsedCharacteristics(inference)) {
				cstics2Reference.put(cstic, inference);
			}
		}
		
		// Remove the referenced characteristics
		for(ConstraintRestriction currentRestriction : source.getRestrictions()) {
			for(Characteristic cstic : expressionExtensions.getUsedCharacteristics(currentRestriction)) {
				cstics2Reference.remove(cstic);
			}
		}
		
		// Show errors for not referenced characteristics
		for(Entry<Characteristic, CharacteristicReference_C> entrySet : cstics2Reference.entrySet()) {
			error("Inferred characteristic " + entrySet.getKey().getName() + " is not mentioned in the restrictions part.", 
					entrySet.getValue(), VcmlPackage.eINSTANCE.getObjectCharacteristicReference_Characteristic(), ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
		}
	}

	@Check
	public void checkInCondition(InCondition_C cond) {
		CharacteristicReference_C cRef = cond.getCharacteristic();
		if (cRef instanceof ObjectCharacteristicReference) {
			checkInCondition_Characteristic(((ObjectCharacteristicReference)cRef).getCharacteristic());
		} else if (cRef instanceof ShortVarReference) {
			checkInCondition_Characteristic(((ShortVarReference)cRef).getRef().getCharacteristic());
		}
	}

	private void checkInCondition_Characteristic(Characteristic characteristic) {
		if (characteristic.isMultiValue()) {
			error("Multivalued characteristic " + characteristic.getName() + " must not be used in 'in' condition", VcmlPackage.Literals.IN_CONDITION_C__CHARACTERISTIC);
		}
	}
}
