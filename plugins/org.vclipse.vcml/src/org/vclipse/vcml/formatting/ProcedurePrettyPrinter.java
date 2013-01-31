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
package org.vclipse.vcml.formatting;

import org.eclipse.emf.common.util.EList;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.ConditionSource;
import org.vclipse.vcml.vcml.CountParts;
import org.vclipse.vcml.vcml.DelDefault;
import org.vclipse.vcml.vcml.InCondition_P;
import org.vclipse.vcml.vcml.IsInvisible;
import org.vclipse.vcml.vcml.IsSpecified_P;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.MDataCharacteristic_P;
import org.vclipse.vcml.vcml.PFunction;
import org.vclipse.vcml.vcml.ProcedureSource;
import org.vclipse.vcml.vcml.SetDefault;
import org.vclipse.vcml.vcml.SetPricingFactor;
import org.vclipse.vcml.vcml.Statement;
import org.vclipse.vcml.vcml.SumParts;
import org.vclipse.vcml.vcml.TypeOf;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.NoExceptions;

public class ProcedurePrettyPrinter extends CodePrettyPrinter {
	
	@Override
	public DataLayouter<NoExceptions> caseConditionSource(ConditionSource object) {
		precedenceLevel = PREC_MAX;
		layouter.beginC(0);
		doSwitch(object.getCondition()).print(".");
		return layouter.end();
	}
	
	@Override
	public DataLayouter<NoExceptions> caseIsInvisible(IsInvisible object) {
		layouter.print("$self.");
		printCrossReference(object, VCMLPACKAGE.getIsInvisible_Characteristic(), VCMLPACKAGE.getVCObject_Name());
		return layouter.print(" is").print(" invisible");
	}

	public DataLayouter<NoExceptions> casePFunction(PFunction object) {
		layouter.beginC().print("pfunction ");
		printCrossReference(object, VCMLPACKAGE.getPFunction_Function(), VCMLPACKAGE.getVCObject_Name());
		layouter.print(" (").brk();
		EList<Characteristic> cstics = object.getCharacteristics();
		EList<Literal> literals = object.getValues();
		boolean start = true;
		for(int i=0; i<cstics.size(); i++) {
			if(start) {
				start = false;
			} else {
				layouter.print(",").brk();
			}
			layouter.beginI();
			printCrossReference(object, cstics.get(i), VCMLPACKAGE.getPFunction_Characteristics(), VCMLPACKAGE.getVCObject_Name());
			layouter.brk().print("=").brk();
			doSwitch(literals.get(i));
			layouter.end();
		}
		return layouter.brk(1,-INDENTATION).print(")").end();
	}

	@Override
	public DataLayouter<NoExceptions> caseDelDefault(DelDefault object) {
		layouter.beginC();
		layouter.print("$del_default").print("(").print("$self,").brk();
		printCrossReference(object, VCMLPACKAGE.getSetOrDelDefault_Characteristic(), VCMLPACKAGE.getVCObject_Name());
		layouter.print(",").brk();
		precedenceLevel = PREC_MAX;
		doSwitch(object.getExpression());
		layouter.print(")");
		return layouter.end();
	}
	
	@Override
	public DataLayouter<NoExceptions> caseSetDefault(SetDefault object) {
		layouter.beginC();
		layouter.print("$self.");
		printCrossReference(object, VCMLPACKAGE.getSetOrDelDefault_Characteristic(), VCMLPACKAGE.getVCObject_Name());
		layouter.brk();
		layouter.print("?=");
		layouter.brk();
		precedenceLevel = PREC_MAX;
		doSwitch(object.getExpression());
		layouter.end();
		return layouter;
	}

	@Override
	public DataLayouter<NoExceptions> caseSetPricingFactor(SetPricingFactor object) {
		layouter.print("$set_pricing_factor (");
		printNullsafe(object.getLocation().getName());
		layouter.print(", ");
		printCrossReference(object, VCMLPACKAGE.getSetPricingFactor_Characteristic(), VCMLPACKAGE.getVCObject_Name());
		layouter.print(", ");
		precedenceLevel = PREC_MAX;
		doSwitch(object.getArg1());
		layouter.print(", ");
		precedenceLevel = PREC_MAX;
		doSwitch(object.getArg2());
		return layouter.print(")");
	}

	@Override
	public DataLayouter<NoExceptions> caseProcedureSource(ProcedureSource object) {
		EList<Statement> statements = object.getStatements();
		if(!statements.isEmpty()) {
			layouter.beginC(0);
			for(int i=0, size=statements.size()-1; i<=size; i++) {
				doSwitch(statements.get(i));
				if(i < size) {
					layouter.print(",").brk();
				} else {
					layouter.print(".");
				}
			}
			layouter.end();
		}
		return layouter;
	}

	@Override
	public DataLayouter<NoExceptions> caseCountParts(CountParts object) {
		return layouter.brk().print("$count_parts (").print(
				object.getLocation().getLiteral().toUpperCase()).print(")");
	}

	@Override
	public DataLayouter<NoExceptions> caseSumParts(SumParts object) {
		layouter.print("$sum_parts").print("(").print(object.getLocation().getLiteral().toUpperCase()).print(", ");
		printNullsafe(object.getCharacteristic().getName());
		return layouter.print(")");
	}

	@Override
	public DataLayouter<NoExceptions> caseMDataCharacteristic_P(MDataCharacteristic_P object) {
		layouter.print("mdata ");
		doSwitch(object.getCharacteristic());
		return layouter;
	}

	@Override
	public DataLayouter<NoExceptions> caseInCondition_P(InCondition_P object) {
		layouter.beginI();
		doSwitch(object.getCharacteristic());
		layouter.brk().print("in").brk();
		doSwitch(object.getList());
		return layouter.end();
	}

	@Override
	public DataLayouter<NoExceptions> caseIsSpecified_P(IsSpecified_P object) {
		layouter.print("specified ");
		doSwitch(object.getCharacteristic());
		return layouter;
	}
	
	@Override
	public DataLayouter<NoExceptions> caseCharacteristicReference_P(CharacteristicReference_P object) {
		String locationPrefix = object.getLocation() != null ? object.getLocation().getLiteral() + "." : "";
		layouter.print(locationPrefix);
		printCrossReference(object, VCMLPACKAGE.getCharacteristicReference_P_Characteristic(), VCMLPACKAGE.getVCObject_Name());
		return layouter;
	}

	@Override
	public DataLayouter<NoExceptions> caseTypeOf(TypeOf object) {
		layouter.print("type_of(").print(object.getLocation().getLiteral().toUpperCase());
		doSwitch(object.getVariantclass());
		return layouter.print(")");
	}
}
