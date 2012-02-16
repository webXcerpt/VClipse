package org.vclipse.vcml.diff;

import org.eclipse.emf.compare.match.engine.AbstractSimilarityChecker;
import org.eclipse.emf.compare.match.engine.internal.EcoreIDSimilarityChecker;
import org.eclipse.emf.compare.match.statistic.MetamodelFilter;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.vcml.vcml.VCObject;

@SuppressWarnings("restriction")
public class VcmlSimilarityCheckerId extends EcoreIDSimilarityChecker {

	public VcmlSimilarityCheckerId(MetamodelFilter filter, AbstractSimilarityChecker fallback) {
		super(filter, fallback);
	}

	@Override
	protected String computeID(EObject object) {
		if(object instanceof VCObject) {
			return object.getClass().getName().concat(((VCObject)object).getName());
		}
		return null;
	}
}
