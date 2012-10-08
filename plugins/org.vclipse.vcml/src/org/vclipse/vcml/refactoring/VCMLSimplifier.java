package org.vclipse.vcml.refactoring;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.core.RefactoringExecuter;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.Condition;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Lists;

public class VCMLSimplifier extends RefactoringExecuter {
 
	protected final VcmlFactory VCML_FACTORY = VcmlFactory.eINSTANCE;
	protected final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;
	
	/*
	 * Re-factoring: extracts common conditions from restrictions to a 
	 * condition part of a constraint source. 
	 */
	public void refactoring_Extract_ConstraintSource(IRefactoringContext context) {
		EObject sourceElement = context.getSourceElement();
		sourceElement = EcoreUtil2.getContainerOfType(sourceElement, ConstraintSource.class);
		if(sourceElement instanceof ConstraintSource) {
			ConstraintSource source = (ConstraintSource)sourceElement;
			extract_Condition(source);
			context.setSourceElement(source);
		}
	}
	
	public void extract_Condition(ConstraintSource source) {
		ConditionalConstraintRestriction previous = null; 
		EList<ConstraintRestriction> restrictions = source.getRestrictions();
		for(ConstraintRestriction restriction : restrictions) {
			if(previous == null) {
				if(!(restriction instanceof ConditionalConstraintRestriction)) {
					break;
				}
				previous = (ConditionalConstraintRestriction)restriction;
				continue;
			} 
			if(restriction instanceof ConditionalConstraintRestriction) {
				ConditionalConstraintRestriction ccr = (ConditionalConstraintRestriction)restriction;
				if(EcoreUtil.equals(previous.getCondition(), ccr.getCondition())) {
					previous = ccr;
				} else {
					previous = null;
					break;
				}
			}
		}
		if(previous != null) {
			Condition condition = EcoreUtil.copy(previous.getCondition());
			source.setCondition(condition);			
			List<ConstraintRestriction> newRestrictions = Lists.newArrayList();
			for(ConstraintRestriction restriction : restrictions) {
				ConditionalConstraintRestriction ccr = (ConditionalConstraintRestriction)restriction;
				ConstraintRestriction entry = ccr.getRestriction();
				newRestrictions.add(EcoreUtil.copy(entry));
			}
			source.getRestrictions().clear();
			source.getRestrictions().addAll(newRestrictions);
		}
	}
	
	/*
	 * Re-factoring: executes inline operation for each restriction 
	 */
	public void refactoring_Inline_ConstraintSource(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		ConstraintSource source = EcoreUtil2.getContainerOfType(element, ConstraintSource.class);
		inline_Condition(source);
		context.setSourceElement(source);
	}
	
	public void inline_Condition(ConstraintSource source) {
		Comparison previous = null;
		EList<ConstraintRestriction> restrictions = source.getRestrictions();
		for(ConstraintRestriction restriction : restrictions) {
			if(previous == null) {
				if(!(restriction instanceof Comparison)) {
					break;
				}
				previous = (Comparison)restriction;
				continue;
			} 
		}
		if(previous != null) {
			Condition condition = source.getCondition();
			source.setCondition(null);
			List<ConstraintRestriction> newRestrictions = Lists.newArrayList();
			for(ConstraintRestriction restriction : restrictions) {
				ConditionalConstraintRestriction ccr = VCML_FACTORY.createConditionalConstraintRestriction();
				ccr.setCondition(EcoreUtil.copy(condition));
				ccr.setRestriction(EcoreUtil.copy(restriction));
				newRestrictions.add(ccr);
			}
			restrictions.clear();
			restrictions.addAll(newRestrictions);
		}
	}
}
