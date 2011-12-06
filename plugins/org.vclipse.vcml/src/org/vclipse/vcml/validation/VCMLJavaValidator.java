/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.vclipse.vcml.documentation.VCMLDescriptionProvider;
import org.vclipse.vcml.utils.ConstraintRestrictionExtensions;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.BinaryExpression;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicReference_C;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.CharacteristicType;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.CompoundStatement;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.ConditionalStatement;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.DelDefault;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Expression;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.InCondition_P;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.MDataCharacteristic_C;
import org.vclipse.vcml.vcml.MDataCharacteristic_P;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.NumberListEntry;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.ObjectCharacteristicReference;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.Row;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.ShortVarReference;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.vcml.UnaryExpression;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.vcml.VariantTableContent;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class VCMLJavaValidator extends AbstractVCMLJavaValidator {

	private static final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;

	@Inject
	private VCMLDescriptionProvider descriptionProvider;
	
	@Inject 
	private ConstraintRestrictionExtensions expressionExtensions;
	 
	private static final int MAXLENGTH_CLASS_CHARACTERISTICS = 999; // SAP limit because cstic index in class table has size 3
	private static final int MAXLENGTH_CLASS_NAME = 18;
	private static final int MAXLENGTH_NAME = 30;
	private static final int MAXLENGTH_DESCRIPTION = 30;
	private static final int MAXLENGTH_DEPENDENCYNET_CHARACTERISTICS = 50; // soft limit of size of dependency net (should not be larger because compilation has a O(n^2) algorithm)
	//private static final int MAXLENGTH_MATERIAL_NAME = 18;

	/*
	 * @Check(CheckType.EXPENSIVE) //executed upon validate action in context menu
     * @Check(CheckType.NORMAL) //upon save
     * @Check(CheckType.FAST) //while editig 
	 */
	
	@Check(CheckType.FAST)
	public void checkCharacteristic(final Characteristic object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of characteristic is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
//		if (object.isMultiValue() && object.isRestrictable()) {
//			error("Multivalued characteristic " + object.getName() + " must not be restrictable", VcmlPackage.Literals.CHARACTERISTIC__RESTRICTABLE);
//		}
	}

	@Check(CheckType.FAST)
	public void checkProcedure(final Procedure object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of procedure is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}

	@Check(CheckType.FAST)
	public void checkDependencyNet(final DependencyNet object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of dependency net is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
		if (object.getConstraints().size() > MAXLENGTH_DEPENDENCYNET_CHARACTERISTICS) {
			warning("Dependency net " + object.getName() + " too large, should have for efficiency at most " + MAXLENGTH_DEPENDENCYNET_CHARACTERISTICS + " constraints", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}

	@Check(CheckType.FAST)
	public void checkConstraint(final Constraint object) {
		if(object.getName().length() > MAXLENGTH_NAME) {
			error("Name of constraint is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
		ConstraintSource source = object.getSource();
		if(source!=null) {
			List<ConstraintRestriction> restrictions = source.getRestrictions();
			int size = Iterables.size(Iterables.filter(restrictions, ConditionalConstraintRestriction.class));
			if(size > 0 && restrictions.size() > size) {
				error("Conditional and unconditional restrictions in the constraint " + object.eGet(VCML_PACKAGE.getVCObject_Name()) + 
						".", VCML_PACKAGE.getVCObject_Name());
				// TODO possible quickfix: split this constraint
			}
		}
	}
	
	@Check(CheckType.FAST)
	public void checkNotRestrictedInferences(Constraint constraint) {
		// Collect the existing characteristics in the inferences part
		ConstraintSource source = constraint.getSource();
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

	@Check(CheckType.FAST)
	public void checkSelectionCondition(final SelectionCondition object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of selection condition is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}

	@Check(CheckType.FAST)
	public void checkPrecondition(final Precondition object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of precondition is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}

	@Check(CheckType.FAST)
	public void checkClass(final Class object) {
		if (VcmlUtils.getClassName(object.getName()).length() > MAXLENGTH_CLASS_NAME) {
			error("Name of class is limited to " + MAXLENGTH_CLASS_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
		if (object.getCharacteristics().size() > MAXLENGTH_CLASS_CHARACTERISTICS) {
			error("Number of characteristics of a class is limited to " + MAXLENGTH_CLASS_CHARACTERISTICS, VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}

	@Check(CheckType.FAST)
	public void checkDescription(final SimpleDescription desc) {
		if (desc.eContainer() instanceof Material) {
			if (desc.getValue().length() > 40) {
				warning("Material descriptions are limited to 40 characters", VcmlPackage.Literals.SIMPLE_DESCRIPTION__VALUE);
			}
		} else {
			if (desc.getValue().length() > MAXLENGTH_DESCRIPTION) {
				warning("Descriptions are limited to " + MAXLENGTH_DESCRIPTION + " characters", VcmlPackage.Literals.SIMPLE_DESCRIPTION__VALUE);
			}
		}
	}
	
	@Check(CheckType.FAST)
	public void checkValueDescription(SymbolicType type) {
		// code adapted from http://blogs.itemis.de/stundzig/archives/487
		Map<String, CharacteristicValue> descriptions = new HashMap<String, CharacteristicValue>();
		Set<String> duplicateDescriptions = Sets.newHashSet();
		
		Map<String, CharacteristicValue> names = new HashMap<String, CharacteristicValue>();
		Set<String> duplicateNames = Sets.newHashSet();
		for(CharacteristicValue value : ((SymbolicType)type).getValues()) {
			String name = value.getName();			
			if(names.get(name) != null) {
				duplicateNames.add(name);
				error("Duplicate value " + name, value, VcmlPackage.Literals.CHARACTERISTIC_VALUE__NAME, ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
			} else {
				names.put(name, value);
			}
			
			String description = descriptionProvider.getDocumentation(value);
			if (description!=null) {
				if(descriptions.get(description) != null) {
					duplicateDescriptions.add(description);
					error("Duplicate description '" + description + "' for value " + name, value, VcmlPackage.Literals.CHARACTERISTIC_VALUE__DESCRIPTION, ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
				} else {
					descriptions.put(description, value);
				}
			}
		}
		for(String description : duplicateDescriptions) {
			error("Duplicate description '" + description + "' for value " + descriptions.get(description).getName(), descriptions.get(description), VcmlPackage.Literals.CHARACTERISTIC_VALUE__DESCRIPTION, ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
		}
		for(String name : duplicateNames) {
			error("Duplicate value " + name, names.get(name), VcmlPackage.Literals.CHARACTERISTIC_VALUE__NAME, ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
		}
	}
	
	@Check(CheckType.FAST)
	public void checkVaraiantTableContents(final VariantTableContent content) {
		EList<VariantTableArgument> parameters = content.getTable().getArguments();
		EList<Row> rows = content.getRows();
		for(Row row : rows) {
			EList<Literal> values = row.getValues();
			if(values.size() != parameters.size()) {
				error("Illegal number of values in a row. Expected " + parameters.size() + " values.", VcmlPackage.Literals.VARIANT_TABLE_CONTENT__ROWS, rows.indexOf(row));
			}
			for(Literal value : values) {
				int index = values.indexOf(value);
				if(index < parameters.size()) {
					Characteristic param = parameters.get(index).getCharacteristic();
					if(!param.eIsProxy() && !contains(param.getType(), value)) {
						error("Parameter " + param.getName() + " does not contain value " + VcmlUtils.getLiteralName(value), 
								row, VcmlPackage.Literals.ROW__VALUES, index);
					}
				}
			}
		}
	}
	
	private boolean contains(CharacteristicType type, Literal value) {
		if(value instanceof SymbolicLiteral && type instanceof SymbolicType) {
			String strValue = ((SymbolicLiteral)value).getValue();
			for(CharacteristicValue cv : ((SymbolicType)type).getValues()) {
				if(cv.getName().equals(strValue)) {
					return true;
				}
			}
		} else if(value instanceof NumericLiteral && type instanceof NumericType) {
			String strValue = ((NumericLiteral)value).getValue();
			for(NumericCharacteristicValue ncv : ((NumericType)type).getValues()) {
				NumberListEntry entry = ncv.getEntry();
				if (entry instanceof NumericLiteral) {
					if(((NumericLiteral)entry).getValue().equals(strValue)) {
						return true;
					}
				}
				// TODO check intervals
			}
		} 
		return false;
	}
	
	@Check
	public void checkComparison(Comparison comparison) {
		Expression left = comparison.getLeft();
		Expression right = comparison.getRight();
		if (isConstant(left) && isConstant(right)) {
			error("Simple condition without variables not allowed.", VcmlPackage.Literals.COMPARISON__OPERATOR);
		}
	}

	private boolean isConstant(Expression expression) {
		if (expression instanceof CharacteristicReference_C || expression instanceof CharacteristicReference_P || expression instanceof MDataCharacteristic_C || expression instanceof MDataCharacteristic_P) {
			return false;
		}
		if (expression instanceof NumericLiteral || expression instanceof SymbolicLiteral) {
			return true;
		}
		if (expression instanceof BinaryExpression) {
			return isConstant(((BinaryExpression)expression).getLeft()) && isConstant(((BinaryExpression)expression).getRight());
		}
		if (expression instanceof UnaryExpression) {
			return isConstant(((UnaryExpression)expression).getExpression());
		}
		// TODO add other uses
		return false;
	}
	
	@Check
	public void checkCompoundStatement(CompoundStatement cs) {
		if (!(cs.eContainer() instanceof ConditionalStatement)) {
			error("Parethenseses around statements can only be used for conditional statements (with an IF).", VcmlPackage.Literals.COMPOUND_STATEMENT__STATEMENTS);
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

	// TODO does this also hold for procedures?
	@Check
	public void checkInCondition(InCondition_P cond) {
		CharacteristicReference_P cRef = cond.getCharacteristic();
		checkInCondition_Characteristic(cRef.getCharacteristic());
	}

	private void checkInCondition_Characteristic(Characteristic characteristic) {
		if (characteristic.isMultiValue()) {
			error("Multivalued characteristic " + characteristic.getName() + " must not be used in 'in' condition", VcmlPackage.Literals.IN_CONDITION_C__CHARACTERISTIC);
		}
	}

	@Check
	public void checkDelDefault(DelDefault dd) {
		Expression expression = dd.getExpression();
		if (expression instanceof CharacteristicReference_P) {
			CharacteristicReference_P cRef = (CharacteristicReference_P)expression;
			Characteristic characteristic = cRef.getCharacteristic();
			if (characteristic.isMultiValue()) {
				error("Multivalued characteristic " + characteristic.getName() + " must not be used in 'del_default' statements", VcmlPackage.Literals.SET_OR_DEL_DEFAULT__EXPRESSION);
			}
		}
	}
	
}