package org.vclipse.vcml.diff.engines;

import org.eclipse.emf.compare.FactoryException;
import org.eclipse.emf.compare.match.engine.AbstractSimilarityChecker;
import org.eclipse.emf.compare.match.statistic.MetamodelFilter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.util.Strings;
import org.vclipse.vcml.vcml.VCObject;

public class VcmlSimilarityChecker extends AbstractSimilarityChecker {

	private AbstractSimilarityChecker delegateSimilarityChecker;
	
	public VcmlSimilarityChecker(MetamodelFilter metamodelFilter, AbstractSimilarityChecker delegateSimilarityChecker) {
		super(metamodelFilter);
		this.delegateSimilarityChecker = delegateSimilarityChecker;
	}

	@Override
	public boolean isSimilar(EObject obj1, EObject obj2)
			throws FactoryException {
		if (isSameVcmlObject(obj1, obj2)) {
			return true;
		}
		return delegateSimilarityChecker.isSimilar(obj1, obj2);
	}

	private boolean isSameVcmlObject(EObject obj1, EObject obj2) {
		if (obj1 instanceof VCObject && obj2 instanceof VCObject && obj1.getClass() == obj2.getClass()) {
			return Strings.equal(((VCObject)obj1).getName(), ((VCObject)obj2).getName());
		}
		return false;
	}

	@Override
	public void init(EObject leftObject, EObject rightObject)
			throws FactoryException {
		delegateSimilarityChecker.init(leftObject, rightObject);
		
	}

	@Override
	public void init(Resource leftResource, Resource rightResource)
			throws FactoryException {
		delegateSimilarityChecker.init(leftResource, rightResource);
	}

	@Override
	public double absoluteMetric(EObject obj1, EObject obj2)
			throws FactoryException {
		if (isSameVcmlObject(obj1, obj2)) {
			return 1d;
		}
		return delegateSimilarityChecker.absoluteMetric(obj1, obj2);
	}

}
