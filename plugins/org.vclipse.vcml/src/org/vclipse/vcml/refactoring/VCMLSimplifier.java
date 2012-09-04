package org.vclipse.vcml.refactoring;

import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.change.ListChange;
import org.eclipse.emf.ecore.change.util.ListDifferenceAnalyzer;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.vclipse.refactoring.core.Refactoring;
import org.vclipse.vcml.vcml.Condition;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlPackage;

public class VCMLSimplifier extends Refactoring {

	protected final VcmlFactory VCML_FACTORY = VcmlFactory.eINSTANCE;
	protected final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;
	
	public void simplify(ConstraintSource source, List<EObject> changes) {
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
			Condition condition = previous.getCondition();
			changes.add(getChangeDescription(source, VCML_PACKAGE.getConstraintSource_Condition(), condition));
			EList<ConstraintRestriction> newRestrictions = new BasicEList<ConstraintRestriction>(restrictions.size());
			for(ConstraintRestriction restriction : restrictions) {
				newRestrictions.add(((ConditionalConstraintRestriction)restriction).getRestriction());
			}
			ListDifferenceAnalyzer analyzer = new ListDifferenceAnalyzer();
			for(ListChange listChange : analyzer.analyzeLists(restrictions, newRestrictions)) {
				listChange.applyAndReverse(getObjectList(restrictions));
				changes.add(listChange);
			}
		}
	}
}
