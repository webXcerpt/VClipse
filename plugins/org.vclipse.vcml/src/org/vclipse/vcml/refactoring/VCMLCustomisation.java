package org.vclipse.vcml.refactoring;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.EcoreUtil2;
import org.vclipse.refactoring.core.IRefactoringContext;
import org.vclipse.refactoring.core.RefactoringCustomisation;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.MDataCharacteristic_P;
import org.vclipse.vcml.vcml.PFunction;

import com.google.inject.Inject;

public class VCMLCustomisation extends RefactoringCustomisation {
	
	@Inject
	private VCMLRefactoring vcmlRefactoring;
	
	public boolean evaluate_Extract_ConstraintSource(IRefactoringContext context, ConstraintSource reference) {
		context.setLabel("Extract common conditions from restrictions");
		ConstraintSource source = EcoreUtil2.getContainerOfType(context.getSourceElement(), ConstraintSource.class);
		for(ConstraintRestriction restriction : source.getRestrictions()) {
			if(restriction instanceof ConditionalConstraintRestriction) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	public boolean evaluate_Inline_ConstraintSource(IRefactoringContext context, ConstraintSource source) {
		context.setLabel("Inline conditions for each restriction");
		if(source.getCondition() != null) {
			return Boolean.TRUE;		
		}
		return Boolean.FALSE;
	}
	
	public boolean evaluate_Extract_InCondition_C_list(IRefactoringContext context, InCondition_C reference) {
		context.setLabel("Extract to conditional or expresssion");
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
