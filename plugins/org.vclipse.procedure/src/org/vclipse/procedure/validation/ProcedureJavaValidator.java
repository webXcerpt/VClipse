package org.vclipse.procedure.validation;

import org.eclipse.xtext.validation.Check;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.CompoundStatement;
import org.vclipse.vcml.vcml.ConditionalStatement;
import org.vclipse.vcml.vcml.DelDefault;
import org.vclipse.vcml.vcml.Expression;
import org.vclipse.vcml.vcml.InCondition_P;
import org.vclipse.vcml.vcml.ProcedureSource;
import org.vclipse.vcml.vcml.VcmlPackage;
 

public class ProcedureJavaValidator extends AbstractProcedureJavaValidator {
	
	@Check
	public void checkProcedureSource(ProcedureSource source) {
		checkSource(source);
	}
	
	@Check
	public void checkCompoundStatement(CompoundStatement cs) {
		if (!(cs.eContainer() instanceof ConditionalStatement)) {
			error("Parethenseses around statements can only be used for conditional statements (with an IF).", VcmlPackage.Literals.COMPOUND_STATEMENT__STATEMENTS);
		}
	}
	
	// TODO does this also hold for procedures?
	@Check
	public void checkInCondition(InCondition_P cond) {
		CharacteristicReference_P cRef = cond.getCharacteristic();
		Characteristic characteristic = cRef.getCharacteristic();
		if (characteristic.isMultiValue()) {
			error("Multivalued characteristic " + characteristic.getName() + " must not be used in 'in' condition", VcmlPackage.Literals.IN_CONDITION_P__CHARACTERISTIC);
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
