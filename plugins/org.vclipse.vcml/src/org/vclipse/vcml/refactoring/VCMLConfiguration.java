package org.vclipse.vcml.refactoring;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.core.RefactoringConfiguration;
import org.vclipse.vcml.utils.ConstraintRestrictionExtensions;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.MDataCharacteristic_P;
import org.vclipse.vcml.vcml.PFunction;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.inject.Inject;

import static org.vclipse.vcml.refactoring.ConstraintRefactorings.*;

public class VCMLConfiguration extends RefactoringConfiguration {
	
	@Inject
	private ConstraintRestrictionExtensions cre;
	
	@Inject
	private DependencySourceUtils sourceUtils;
	
	protected static VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;
	
	/**
	 * Features
	 */
	public List<? extends EStructuralFeature> features_Inline_ConstraintSource(IRefactoringContext context, ConstraintSource source) {
		return features_Extract_ConstraintSource(context, source);
	}
	
	public List<? extends EStructuralFeature> features_Extract_ConstraintSource(IRefactoringContext context, ConstraintSource source) {
		return get(VCML_PACKAGE.getConstraint_Source(), VCML_PACKAGE.getConstraintSource_Restrictions());
	}
	
	public List<? extends EStructuralFeature> features_Extract_InCondition_C(IRefactoringContext context) {
		return get(VCML_PACKAGE.getInCondition_C_List());
	}
	
	public List<? extends EStructuralFeature> features_Replace_PFunction(IRefactoringContext context) {
		return get(VCML_PACKAGE.getPFunction_Values());
	}
	
	public boolean initialize_Replace_objects(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		context.setLabel("Delete " + element.eClass().getName().toLowerCase());
		return Boolean.TRUE;
	}
	
	public boolean initialize_Replace_description(IRefactoringContext context) {
		EStructuralFeature feature = context.getStructuralFeature();
		if(VCML_PACKAGE.getVCObject_Description() == feature) {
			context.setLabel("Replace description with a new value");
			return context.getSourceElement() instanceof EObject;
		}
		return Boolean.FALSE;
	}
	
	public boolean initialize_Extract_ConstraintSource(IRefactoringContext context, ConstraintSource source) {
		EStructuralFeature feature = context.getStructuralFeature();
		if(VCML_PACKAGE.getConstraint_Source() == feature) {
			VCObject vcobject = sourceUtils.getDependency(source);
			if(vcobject instanceof Constraint && source.getRestrictions().size() > MIN_CONSTRAINTS_AMOUNT) {
				context.setLabel("Split constraint source");
				return true;
			}
		}
		if(VCML_PACKAGE.getConstraintSource_Restrictions() == feature) {
			context.setLabel("Extract common conditions from restrictions");
			return cre.canExtractCommonConditions(source) != null;
		}
		return false;
	}
	
	public boolean initialize_Inline_ConstraintSource(IRefactoringContext context, ConstraintSource source) {
		if(VCML_PACKAGE.getConstraintSource_Restrictions() == context.getStructuralFeature()) {
			context.setLabel("Inline conditions for each restriction");
			return source.getCondition() != null;
		}
		return false;
	}
	
	public boolean initialize_Extract_InCondition_C_list(IRefactoringContext context, InCondition_C reference) {
		context.setLabel("Extract to conditional or expresssion");
		return Boolean.TRUE;
	}
	
	public boolean initialize_Replace_PFunction_values(IRefactoringContext context, PFunction pfunction) {
		EList<Literal> values = pfunction.getValues();
		for(Literal literal : values) {
			if(literal instanceof MDataCharacteristic_P || literal instanceof CharacteristicReference_P) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
}
