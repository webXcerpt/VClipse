package org.vclipse.vcml.refactoring;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.core.DefaultRefactoringExecuter;
import org.vclipse.vcml.utils.ConstraintRestrictionExtensions;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.Condition;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class VCMLSimplifier extends DefaultRefactoringExecuter {
 
	@Inject
	private ConstraintRestrictionExtensions cre;
	
	protected final VcmlFactory VCML_FACTORY = VcmlFactory.eINSTANCE;
	protected final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;
	
	public EPackage getPackage() {
		return VCML_PACKAGE;
	}

	public Set<EClass> getTopLevelTypes() {
		return Sets.newHashSet(VCML_PACKAGE.getVcmlModel());
	}
	
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
		ConditionalConstraintRestriction ccr = cre.canExtractCommonConditions(source);
		if(ccr != null) {
			EList<ConstraintRestriction> restrictions = source.getRestrictions();
			Condition condition = EcoreUtil.copy(ccr.getCondition());
			source.setCondition(condition);			
			BasicEList<ConstraintRestriction> newRestrictions = new BasicEList<ConstraintRestriction>();
			for(ConstraintRestriction restriction : restrictions) {
				ConditionalConstraintRestriction restriction3 = (ConditionalConstraintRestriction)restriction;
				ConstraintRestriction restriction2 = restriction3.getRestriction();
				ConstraintRestriction copy = EcoreUtil.copy(restriction2);
				newRestrictions.add(copy);
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
