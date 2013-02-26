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
package org.vclipse.vcml

import com.google.common.collect.Lists
import java.util.List
import org.vclipse.vcml.vcml.Characteristic
import org.vclipse.vcml.vcml.Comparison
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction
import org.vclipse.vcml.vcml.Function
import org.vclipse.vcml.vcml.InCondition_C
import org.vclipse.vcml.vcml.IsSpecified_C
import org.vclipse.vcml.vcml.NegatedConstraintRestrictionLHS
import org.vclipse.vcml.vcml.ObjectCharacteristicReference
import org.vclipse.vcml.vcml.ShortVarReference
import org.vclipse.vcml.vcml.Table
import org.vclipse.vcml.vcml.ConstraintSource
import org.eclipse.emf.ecore.util.EcoreUtil
import org.vclipse.vcml.vcml.SymbolicLiteral
import org.vclipse.vcml.vcml.ConstraintRestrictionFalse
import org.vclipse.vcml.vcml.NumericLiteral

class ConstraintRestrictionsUtilities {
	
	def ConditionalConstraintRestriction canExtractCommonConditions(ConstraintSource source) {
		var forReturn = null as ConditionalConstraintRestriction
		for(restriction : source.restrictions) {
			if(forReturn == null) {
				if(restriction instanceof ConditionalConstraintRestriction) {
					forReturn = restriction as ConditionalConstraintRestriction
				} else {
					return forReturn
				}
			} else {
				if(restriction instanceof ConditionalConstraintRestriction) {
					val current = restriction as ConditionalConstraintRestriction
					val condition = forReturn.getCondition();
					val condition2 = current.getCondition();
					if(EcoreUtil::equals(condition, condition2)) {
						forReturn = current
					} else {
						return null
					}
				}
			}
			return forReturn
		}
	}
	
	def dispatch List<Characteristic> usedCstis(Comparison comparison) {
		val cstics = usedCstis(comparison.left)
		cstics.addAll(usedCstis(comparison.right))
		cstics
	}
	
	def dispatch List<Characteristic> usedCstis(ConditionalConstraintRestriction restriction) {
		usedCstis(restriction.restriction)
	}
	
	def dispatch List<Characteristic> usedCstis(ObjectCharacteristicReference reference) {
		return Lists::newArrayList(reference.characteristic)
	}
	
	def dispatch List<Characteristic> usedCstis(ShortVarReference reference) {
		return Lists::newArrayList(reference.ref.characteristic)
	}
	
	def dispatch List<Characteristic> usedCstis(InCondition_C inCondition) {
		usedCstis(inCondition.characteristic)
	}
	
	def dispatch List<Characteristic> usedCstis(IsSpecified_C isSpecified) {
		usedCstis(isSpecified.characteristic)
	}
	
	def dispatch List<Characteristic> usedCstis(NegatedConstraintRestrictionLHS restriction) {
		usedCstis(restriction.restriction)
	}
	
	def dispatch List<Characteristic> usedCstis(Table table) {
		val cstics = Lists::<Characteristic>newArrayList
		for(literal : table.values) {
			val usedCstics = usedCstis(literal)
			cstics.addAll(usedCstics)
		}
		cstics
	}
	
	def dispatch List<Characteristic> usedCstis(Function function) {
		val cstics = Lists::<Characteristic>newArrayList
		for(literal : function.values) {
			val usedCstics = usedCstis(literal)
			cstics.addAll(usedCstics)
		}
		cstics
	}
	
	def dispatch List<Characteristic> usedCstis(SymbolicLiteral obj) {
		Lists::<Characteristic>newArrayList // TODO check whether this is correct
	}
	
	def dispatch List<Characteristic> usedCstis(NumericLiteral obj) {
		Lists::<Characteristic>newArrayList // TODO check whether this is correct
	}
	
	def dispatch List<Characteristic> usedCstis(ConstraintRestrictionFalse obj) {
		Lists::<Characteristic>newArrayList // TODO check whether this is correct
	}
	
}