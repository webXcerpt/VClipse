package org.vclipse.vcml.refactoring;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.core.RefactoringCustomisation;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.MDataCharacteristic_P;
import org.vclipse.vcml.vcml.PFunction;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.inject.Inject;

public class VCMLCustomisation extends RefactoringCustomisation {
	
	@Inject
	private VCMLRefactoring vcmlRefactoring;
	
	protected static VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;
	
	/**
	 * Features
	 */
	public List<? extends EStructuralFeature> features_Inline_ConstraintSource(IRefactoringContext context) {
		return features_Extract_ConstraintSource(context);
	}
	
	public List<? extends EStructuralFeature> features_Extract_ConstraintSource(IRefactoringContext context) {
		return get(VCML_PACKAGE.getConstraintSource_Restrictions());
	}
	
	public List<? extends EStructuralFeature> features_Extract_InCondition_C(IRefactoringContext context) {
		return get(VCML_PACKAGE.getInCondition_C_List());
	}
	
	public List<? extends EStructuralFeature> features_Replace_PFunction(IRefactoringContext context) {
		return get(VCML_PACKAGE.getPFunction_Values());
	}
	
	public boolean init_Replace_objects(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		context.setLabel("Delete " + element.eClass().getName().toLowerCase());
		return Boolean.TRUE;
	}
	
	/**
	 * 
	 */
	public boolean init_Extract_ConstraintSource(IRefactoringContext context, ConstraintSource source) {
		context.setLabel("Extract common conditions from restrictions");
		EList<ConstraintRestriction> restrictions = source.getRestrictions();
		if(restrictions.size() > 1) {
			for(ConstraintRestriction restriction : restrictions) {
				if(restriction instanceof ConditionalConstraintRestriction) {
					return Boolean.TRUE;
				}
			}			
		}
		return Boolean.FALSE;
	}
	
	public boolean init_Inline_ConstraintSource(IRefactoringContext context, ConstraintSource source) {
		context.setLabel("Inline conditions for each restriction");
		if(source.getCondition() != null) {
			return Boolean.TRUE;		
		}
		return Boolean.FALSE;
	}
	
	public boolean init_Extract_InCondition_C_list(IRefactoringContext context, InCondition_C reference) {
		context.setLabel("Extract to conditional or expresssion");
		return Boolean.TRUE;
	}
	
	public boolean init_Replace_PFunction_values(IRefactoringContext context, PFunction pfunction) {
		EList<Literal> values = pfunction.getValues();
		for(Literal literal : values) {
			if(literal instanceof MDataCharacteristic_P || literal instanceof CharacteristicReference_P) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
}
