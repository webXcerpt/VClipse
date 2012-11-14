package org.vclipse.vcml.utils;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.util.PolymorphicDispatcher;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.Function;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.IsSpecified_C;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.NegatedConstraintRestrictionLHS;
import org.vclipse.vcml.vcml.ObjectCharacteristicReference;
import org.vclipse.vcml.vcml.ShortVarReference;
import org.vclipse.vcml.vcml.Table;

import com.google.common.collect.Lists;

public class ConstraintRestrictionExtensions {
	
	private final PolymorphicDispatcher<List<Characteristic>> csticExtractor 
		= new PolymorphicDispatcher<List<Characteristic>>("usedCharacteristics", 1, 1, Collections.singletonList(this),
			PolymorphicDispatcher.NullErrorHandler.<List<Characteristic>> get()) {
		@Override
		protected List<Characteristic> handleNoSuchMethod(final Object... params) {
			return Lists.newArrayList();
		}
	};
	
	public ConditionalConstraintRestriction canExtractCommonConditions(ConstraintSource source) {
		ConditionalConstraintRestriction forReturn = null;
		for(ConstraintRestriction restriction : source.getRestrictions()) {
			if(forReturn == null) {
				if(!(restriction instanceof ConditionalConstraintRestriction)) {
					break;
				}
				forReturn = (ConditionalConstraintRestriction)restriction;
				continue;
			}
			if(restriction instanceof ConditionalConstraintRestriction) {
				ConditionalConstraintRestriction current = (ConditionalConstraintRestriction)restriction;
				if(EcoreUtil.equals(forReturn.getCondition(), current.getCondition())) {
					forReturn = current;
				} else {
					forReturn = null;
					break;
				}
			}
		}
		return forReturn;
	}
	
	public List<Characteristic> getUsedCharacteristics(EObject object) {
		return csticExtractor.invoke(object);
	}
	
	protected List<Characteristic> usedCharacteristics(Comparison comparison) {
		List<Characteristic> cstics = getUsedCharacteristics(comparison.getLeft());
		cstics.addAll(getUsedCharacteristics(comparison.getRight()));
		return cstics;
	}
	
	protected List<Characteristic> usedCharacteristics(ConditionalConstraintRestriction restriction) {
		return getUsedCharacteristics(restriction.getRestriction());
	}
	
	protected List<Characteristic> usedCharacteristics(ObjectCharacteristicReference reference) {
		return Lists.newArrayList(reference.getCharacteristic());
	}
	
	protected List<Characteristic> usedCharacteristics(ShortVarReference reference) {
		return Lists.newArrayList(reference.getRef().getCharacteristic());
	}
	
	protected List<Characteristic> usedCharacteristics(InCondition_C inCondition) {
		return getUsedCharacteristics(inCondition.getCharacteristic());
	}
	
	protected List<Characteristic> usedCharacteristics(IsSpecified_C isSpecified) {
		return getUsedCharacteristics(isSpecified.getCharacteristic());
	}
	
	protected List<Characteristic> usedCharacteristics(NegatedConstraintRestrictionLHS restriction) {
		return getUsedCharacteristics(restriction.getRestriction());
	}
	
	protected List<Characteristic> usedCharacteristics(Table table) {
		List<Characteristic> cstics = Lists.newArrayList();
		for(Literal literal : table.getValues()) {
			cstics.addAll(getUsedCharacteristics(literal));
		}
		return cstics;
	}
	
	protected List<Characteristic> usedCharacteristics(Function function) {
		List<Characteristic> cstics = Lists.newArrayList();
		for(Literal literal : function.getValues()) {
			cstics.addAll(getUsedCharacteristics(literal));
		}
		return cstics;
	}
}
