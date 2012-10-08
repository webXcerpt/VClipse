package org.vclipse.vcml.refactoring;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.core.RefactoringExecuter;
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
	
//	
//	public List<? extends EObject> refactoring_Inline_ConstraintSource(IRefactoringContext context) {
//		List<EObject> changes = Lists.newArrayList();
//		inlineCondition(EcoreUtil2.getContainerOfType(context.getSourceElement(), ConstraintSource.class), changes);
//		return changes;
//	}
	
//	public void inlineCondition(ConstraintSource source, List<EObject> changes) {
//		Comparison previous = null;
//		EList<ConstraintRestriction> restrictions = source.getRestrictions();
//		for(ConstraintRestriction restriction : restrictions) {
//			if(previous == null) {
//				if(!(restriction instanceof Comparison)) {
//					break;
//				}
//				previous = (Comparison)restriction;
//				continue;
//			} 
//		}
//		if(previous != null) {
//			Condition condition = source.getCondition();
//			changes.add(getChangeDescription(source, VCML_PACKAGE.getConstraintSource_Condition(), null));
//			EList<ConstraintRestriction> newRestrictions = new BasicEList<ConstraintRestriction>();
//			for(ConstraintRestriction restriction : restrictions) {
//				ConditionalConstraintRestriction ccr = VCML_FACTORY.createConditionalConstraintRestriction();
//				ccr.setCondition(EcoreUtil.copy(condition));
//				ccr.setRestriction(EcoreUtil.copy(restriction));
//				newRestrictions.add(ccr);
//			}
//			ListDifferenceAnalyzer analyzer = new ListDifferenceAnalyzer();
//			for(ListChange listChange : analyzer.analyzeLists(restrictions, newRestrictions)) {
//				listChange.applyAndReverse(getObjectList(restrictions));
//				changes.add(listChange);
//			}
//		}
//	}
}
