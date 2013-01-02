package org.vclipse.vcml.diff.engines;

import org.eclipse.emf.compare.match.engine.AbstractSimilarityChecker;
import org.eclipse.emf.compare.match.engine.internal.EcoreIDSimilarityChecker;
import org.eclipse.emf.compare.match.statistic.MetamodelFilter;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableContent;

@SuppressWarnings("restriction")
public class VcmlSimilarityCheckerId extends EcoreIDSimilarityChecker {
	
	public VcmlSimilarityCheckerId(MetamodelFilter filter, AbstractSimilarityChecker checker) {
		super(filter, checker);
	}

	@Override
	protected String computeID(EObject object) {
		if(object instanceof VCObject) {
			String className = object.eClass().getInstanceClassName();
			if(object instanceof VariantTableContent) {
				VariantTable table = ((VariantTableContent)object).getTable();
				return table == null ? className : className.concat(table.getName());
			}
			return className;
		}
		return null;
	}
}
