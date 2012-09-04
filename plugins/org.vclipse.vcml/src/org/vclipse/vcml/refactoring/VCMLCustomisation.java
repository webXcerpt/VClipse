package org.vclipse.vcml.refactoring;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.vclipse.refactoring.core.EvaluationResult;
import org.vclipse.refactoring.core.IEvaluationResult;
import org.vclipse.refactoring.core.IRefactoringContext;
import org.vclipse.refactoring.core.RefactoringCustomisation;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.MDataCharacteristic_P;
import org.vclipse.vcml.vcml.PFunction;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Lists;

public class VCMLCustomisation extends RefactoringCustomisation {
	
	private static final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;
	
	public List<? extends EStructuralFeature> features_Extract_InCondition_C(IRefactoringContext context, InCondition_C reference) {
		return Lists.newArrayList(VCML_PACKAGE.getInCondition_P_List());
	}
	
	public List<? extends EStructuralFeature> features_Replace_PFunction_values(IRefactoringContext context, PFunction pfunction) {
		return Lists.newArrayList(VCML_PACKAGE.getPFunction_Values());
	}
	
	public IEvaluationResult evaluate_Inline_ConstraintSource(IRefactoringContext context, ConstraintSource reference) {
		return EvaluationResult.getTrueResult(reference);
	}
	
	public IEvaluationResult evaluate_Extract_InCondition_C_list(IRefactoringContext context, InCondition_C reference) {
		return EvaluationResult.getTrueResult(reference, VCML_PACKAGE.getInCondition_P_List());
	}
	
	public IEvaluationResult evaluate_Replace_PFunction_values(IRefactoringContext context, PFunction pfunction) {
		EList<Literal> values = pfunction.getValues();
		for(Literal literal : values) {
			if(literal instanceof MDataCharacteristic_P || literal instanceof CharacteristicReference_P) {
				return EvaluationResult.getTrueResult(pfunction, VCML_PACKAGE.getPFunction_Values());
			}
		}
		return EvaluationResult.getFalseResult(pfunction);
	}
	
	
}
