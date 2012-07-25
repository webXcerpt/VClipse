package org.vclipse.vcml.diff.engines;

import org.eclipse.emf.compare.match.engine.AbstractSimilarityChecker;
import org.eclipse.emf.compare.match.engine.internal.EcoreIDSimilarityChecker;
import org.eclipse.emf.compare.match.statistic.MetamodelFilter;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantTableContent;

@SuppressWarnings("restriction")
public class VcmlSimilarityCheckerId extends EcoreIDSimilarityChecker {
	
	public VcmlSimilarityCheckerId(MetamodelFilter filter, AbstractSimilarityChecker checker) {
		super(filter, checker);
	}

	@Override
	protected String computeID(EObject object) {
		if(object instanceof VCObject) {
			return object.getClass().getName().concat(
					(object instanceof VariantTableContent) ? 
							((VariantTableContent)object).getTable().getName() : ((VCObject)object).getName());
		}
		return null;
	}
}
