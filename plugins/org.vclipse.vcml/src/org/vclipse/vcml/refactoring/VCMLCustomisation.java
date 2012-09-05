package org.vclipse.vcml.refactoring;

import org.eclipse.emf.common.util.EList;
import org.vclipse.refactoring.core.IRefactoringContext;
import org.vclipse.refactoring.core.RefactoringCustomisation;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.MDataCharacteristic_P;
import org.vclipse.vcml.vcml.PFunction;

public class VCMLCustomisation extends RefactoringCustomisation {
	
	public boolean evaluate_Extract_ConstraintSource(IRefactoringContext context, ConstraintSource reference) {
		context.setText("Extract common conditions from restrictions");
		return Boolean.TRUE;
	}
	
	public boolean evaluate_Inline_ConstraintSource(IRefactoringContext context, ConstraintSource source) {
		context.setText("Inline conditions for each restriction");
		if(source.getCondition() != null) {
			return Boolean.TRUE;		
		}
		return Boolean.FALSE;
	}
	
	public boolean evaluate_Extract_InCondition_C_list(IRefactoringContext context, InCondition_C reference) {
		context.setText("Extract to conditional or expresssion");
		return Boolean.TRUE;
	}
	
	public boolean evaluate_Replace_PFunction_values(IRefactoringContext context, PFunction pfunction) {
		EList<Literal> values = pfunction.getValues();
		for(Literal literal : values) {
			if(literal instanceof MDataCharacteristic_P || literal instanceof CharacteristicReference_P) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
}
