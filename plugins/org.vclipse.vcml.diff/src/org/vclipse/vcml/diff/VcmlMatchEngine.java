package org.vclipse.vcml.diff;

import org.eclipse.emf.compare.match.engine.AbstractSimilarityChecker;
import org.eclipse.emf.compare.match.engine.GenericMatchEngine;

public class VcmlMatchEngine extends GenericMatchEngine {

	@Override
	protected AbstractSimilarityChecker prepareChecker() {
		AbstractSimilarityChecker delegateSimilarityChecker = super.prepareChecker();
		return new VcmlSimilarityChecker(filter, delegateSimilarityChecker);
	}
}
