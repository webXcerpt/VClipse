package org.vclipse.refactoring.utils;

import static org.vclipse.refactoring.utils.RefactoringMatchEngine.DIFFERENT;
import static org.vclipse.refactoring.utils.RefactoringMatchEngine.SIMILAR;
import static org.vclipse.refactoring.utils.RefactoringMatchEngine.THRESHOLD_0_2;
import static org.vclipse.refactoring.utils.RefactoringMatchEngine.THRESHOLD_0_5;

import org.eclipse.emf.compare.FactoryException;
import org.eclipse.emf.compare.match.engine.internal.DistinctEcoreSimilarityChecker;
import org.eclipse.emf.compare.match.engine.internal.GenericMatchEngineToCheckerBridge;
import org.eclipse.emf.compare.match.statistic.MetamodelFilter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class RefactoringSimilarityChecker extends DistinctEcoreSimilarityChecker {

	private boolean refactoringExecuted;
	
	private EntrySearch search;
	
	public RefactoringSimilarityChecker(EntrySearch search, MetamodelFilter filter, GenericMatchEngineToCheckerBridge bridge) {
		super(filter, bridge);
		this.search = search;
	}
	
	public void refactoringExecuted(boolean executed) {
		this.refactoringExecuted = executed;
	}
	
	@Override
	public boolean isSimilar(EObject first, EObject second) throws FactoryException {
		if(refactoringExecuted) {
			double absoluteSimilarity = absoluteMetric(first, second);
			double contentSimilarity = contentSimilarity(first, second);
			double nameSimilarity = nameSimilarity(first, second);
			EObject firstContainer = first.eContainer();
			EObject secondContainer = second.eContainer();
			if(SIMILAR == nameSimilarity) {
				return Boolean.TRUE;
			} 
			if(THRESHOLD_0_2 < absoluteSimilarity) {
				return Boolean.FALSE;
			} 
			if(THRESHOLD_0_5 <= contentSimilarity && SIMILAR >= contentSimilarity) {
				return Boolean.TRUE && search.equallyTyped(firstContainer, secondContainer);
			} 
			if(DIFFERENT == contentSimilarity && firstContainer == secondContainer) {
				return search.equallyTyped(first, second);
			} 
			if(absoluteSimilarity <= THRESHOLD_0_2) {
				return SIMILAR == nameSimilarity(firstContainer, secondContainer);
			}
			return SIMILAR == contentSimilarity;
		}
		return EcoreUtil.equals(first, second);
	}
}
