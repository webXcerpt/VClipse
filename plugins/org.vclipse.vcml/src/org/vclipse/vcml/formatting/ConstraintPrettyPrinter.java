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
package org.vclipse.vcml.formatting;

import org.eclipse.emf.common.util.EList;
import org.vclipse.vcml.vcml.CharacteristicReference_C;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintClass;
import org.vclipse.vcml.vcml.ConstraintMaterial;
import org.vclipse.vcml.vcml.ConstraintObject;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintRestrictionFalse;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.IsSpecified_C;
import org.vclipse.vcml.vcml.MDataCharacteristic_C;
import org.vclipse.vcml.vcml.NegatedConstraintRestrictionLHS;
import org.vclipse.vcml.vcml.ObjectCharacteristicReference;
import org.vclipse.vcml.vcml.ObjectType;
import org.vclipse.vcml.vcml.PartOfCondition;
import org.vclipse.vcml.vcml.PartialKey;
import org.vclipse.vcml.vcml.ShortVarDefinition;
import org.vclipse.vcml.vcml.ShortVarReference;
import org.vclipse.vcml.vcml.SubpartOfCondition;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.NoExceptions;

/**
 * 
 */
public class ConstraintPrettyPrinter extends CodePrettyPrinter {
	
	@Override
	public DataLayouter<NoExceptions> caseConstraintSource(ConstraintSource object) {
		layouter.beginC(0);
		layouter.beginC().print("objects:").brk();
		EList<ConstraintObject> cobjects = object.getObjects();
		for(int i=0, size=cobjects.size()-1; i<=size; i++) {
			doSwitch(cobjects.get(i));
			if(i < size) {
				layouter.print(",").brk();
			} else {
				layouter.print(".");
			}
		}
		layouter.brk(1, -INDENTATION).end();
		if(object.getCondition() != null) {
			layouter.brk().beginI().print("condition:").brk();
			precedenceLevel = PREC_MAX;
			doSwitch(object.getCondition());
			layouter.print(".").brk(1, -INDENTATION).end().brk();
		}
		final EList<ConstraintRestriction> restrictions = object.getRestrictions();
		layouter.brk().beginI().print("restrictions:").brk();
		for(int i=0, size=restrictions.size()-1; i<=size; i++) {
			precedenceLevel = PREC_MAX;
			layouter.beginI();
			doSwitch(restrictions.get(i));
			layouter.end();
			if(i<size) {
				layouter.brk().print(",").brk();
			} else {
				layouter.print(".");
			}
		}
		layouter.brk(1, -INDENTATION).end();
		EList<CharacteristicReference_C> cstic_refs = object.getInferences();
		if(!cstic_refs.isEmpty()) {
			layouter.brk().beginI().print("inferences:").brk();
			for(int i=0, size=cstic_refs.size()-1; i<=size; i++) {
				doSwitch(cstic_refs.get(i));
				if(i < size) {
					layouter.print(",").brk();
				} else {
					layouter.print(".");
				}
			}
			layouter.brk(1, -INDENTATION).end();
		}
		return layouter.end();
	}
	
	@Override
	public DataLayouter<NoExceptions> caseConstraintClass(ConstraintClass object) {
		layouter.beginC();
		printNullsafe(object.getName());
		layouter.print(" is_a "); // TODO improve this
		printCrossReference(object, VCMLPACKAGE.getConstraintClass_Class(), VCMLPACKAGE.getVCObject_Name());
		EList<ShortVarDefinition> shortVars = object.getShortVars();
		if(!shortVars.isEmpty()) {
			layouter.print(" where").brk();
			for(int i=0, size=shortVars.size()-1; i<=size; i++) {
				ShortVarDefinition cur = shortVars.get(i);
				printNullsafe(cur.getName());
				layouter.print(" = ");
				printCrossReference(cur, VCMLPACKAGE.getShortVarDefinition_Characteristic(), VCMLPACKAGE.getVCObject_Name());
				if(i<size) {
					layouter.print(";").brk();
				}
			}
		}
		return layouter.brk(1, -INDENTATION).end();
	}

	@Override
	public DataLayouter<NoExceptions> caseConstraintMaterial(ConstraintMaterial object) {
		layouter.beginC();
		ObjectType objectType = object.getObjectType();
		printNullsafe(object.getName());
		layouter.print(" is_object ");
		layouter.print("(" + objectType.getType() + ")").print("(" + objectType.getClassType() + ")");
		layouter.print("(");
		for (PartialKey pKey : objectType.getAttrs()) {
			printNullsafe(pKey.getKey());
			layouter.print("=");
			layouter.print("'" + pKey.getMaterial().getName() + "'");
			// TODO insert commas and brk()
		}
		layouter.print(")");
		EList<ShortVarDefinition> shortVars = object.getShortVars();
		if(!shortVars.isEmpty()) {
			layouter.print(" where").brk();
			for(int i=0, size=shortVars.size()-1; i<=size; i++) {
				ShortVarDefinition definition = shortVars.get(i);
				printNullsafe(definition.getName());
				layouter.print(" = ");
				printCrossReference(definition, VCMLPACKAGE.getShortVarDefinition_Characteristic(), VCMLPACKAGE.getVCObject_Name());
				if(i<size) {
					layouter.print(";");
				}
			}
			layouter.brk(1, -INDENTATION);
		}
		return layouter.end();
	}

	@Override
	public DataLayouter<NoExceptions> caseInCondition_C(InCondition_C object) {
		doSwitch(object.getCharacteristic());
		layouter.print(" in ");
		doSwitch(object.getList());
		return layouter;
	}

	@Override
	public DataLayouter<NoExceptions> caseMDataCharacteristic_C(MDataCharacteristic_C object) {
		layouter.print("mdata ");
		doSwitch(object.getCharacteristic());
		return layouter;
	}

	@Override
	public DataLayouter<NoExceptions> caseIsSpecified_C(IsSpecified_C object) {
		layouter.print("specified ");
		doSwitch(object.getCharacteristic());
		return layouter;
	}

	@Override
	public DataLayouter<NoExceptions> caseNegatedConstraintRestrictionLHS(NegatedConstraintRestrictionLHS object) {
		int parentPrec = precedenceLevel;
		precedenceLevel = PREC_UNARY;
		layouter.print("not ");
		if(parentPrec > precedenceLevel) {
			layouter.print("(");
		}
		doSwitch(object.getRestriction());
		if(parentPrec > precedenceLevel) {
			layouter.print(")");
		}
		precedenceLevel = parentPrec;
		return layouter;
	}

	@Override
	public DataLayouter<NoExceptions> caseObjectCharacteristicReference(ObjectCharacteristicReference object) {
		printNullsafe(object.getLocation().getName() + ".");
		printCrossReference(object, VCMLPACKAGE.getObjectCharacteristicReference_Characteristic(), VCMLPACKAGE.getVCObject_Name());
		return layouter;
	}

	@Override
	public DataLayouter<NoExceptions> caseShortVarReference(ShortVarReference object) {
		printNullsafe(object.getRef().getName());
		return layouter;
	}

	@Override
	public DataLayouter<NoExceptions> casePartOfCondition(PartOfCondition object) {
		layouter.print("part_of").print("(");
		printNullsafe(object.getChild().getName());
		layouter.print(", ");
		printNullsafe(object.getParent().getName());
		return layouter.print(")");
	}

	@Override
	public DataLayouter<NoExceptions> caseSubpartOfCondition(SubpartOfCondition object) {
		layouter.print("subpart_of").print("(");
		printNullsafe(object.getChild().getName());
		layouter.print(", ");
		printNullsafe(object.getParent().getName());
		return layouter.print(")");
	}

	@Override
	public DataLayouter<NoExceptions> caseObjectType(ObjectType object) {
		layouter.print("(" + object.getType() + ")" + object.getClassType());
		boolean start = true;
		for(PartialKey key : object.getAttrs()) {
			if(start) {
				start = false;
			} else {
				layouter.print(", ");
			}
			printNullsafe(key.getKey());
			layouter.print(" = ");
			layouter.print("'");
			printNullsafe(key.getMaterial().getName());
			layouter.print("'");
		}
		return layouter;
	}

	@Override
	public DataLayouter<NoExceptions> caseConstraintRestrictionFalse(final ConstraintRestrictionFalse object) {
		return layouter.print("false");
	}

	@Override
	public DataLayouter<NoExceptions> caseConditionalConstraintRestriction(final ConditionalConstraintRestriction object) {
		layouter.beginI();
		doSwitch(object.getRestriction());
		layouter.brk().print("if").brk();;
		doSwitch(object.getCondition());
		return layouter.end();
	}
}
