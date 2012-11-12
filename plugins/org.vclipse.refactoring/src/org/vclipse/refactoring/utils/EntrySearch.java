/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.refactoring.utils;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.compare.FactoryException;
import org.eclipse.emf.compare.match.engine.internal.DistinctEcoreSimilarityChecker;
import org.eclipse.emf.compare.match.engine.internal.GenericMatchEngineToCheckerBridge;
import org.eclipse.emf.compare.match.statistic.MetamodelFilter;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.vclipse.refactoring.RefactoringPlugin;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class EntrySearch {

	private final Extensions extensions;
	
	private DistinctEcoreSimilarityChecker checker;
	
	private static final double MATCHING = 1.0;
	private static final double NOT_MATCHING = 0.0;
	
	private boolean refactoringConditions;
	
	@Inject
	public EntrySearch(Extensions extensions) {
		this.extensions = extensions;
	}
	
	public void refactoringConditions(boolean enable) {
		this.refactoringConditions = enable;
	}
	
	public List<EObject> getEntries(EObject object) {
		EObject rootContainer = EcoreUtil.getRootContainer(object);
		List<EObject> entries = Lists.newArrayList(object.eAllContents());
		entries.add(0, rootContainer);
		return entries;
	}
	
	public EObject findEntry(EObject object, List<EObject> entries) {
		if(checker == null) {
			MetamodelFilter filter = getMetamodelFilter(object);
			GenericMatchEngineToCheckerBridge bridge = getMatchEngineBridge(object);
			checker = new DistinctEcoreSimilarityChecker(filter, bridge) {
				@Override
				public boolean isSimilar(EObject first, EObject second) throws FactoryException {
					if(!refactoringConditions) {
						return super.isSimilar(first, second) && MATCHING == nameSimilarity(first, second);
					} else {
						double nameSimilarity = nameSimilarity(first, second);
						if(MATCHING == nameSimilarity) {
							return Boolean.TRUE;
						} else {
							double contentSimilarity = contentSimilarity(first, second);
							if(NOT_MATCHING == contentSimilarity) {
								return first.eContainer() == second.eContainer() && equallyTyped(first, second);
							}
							return MATCHING == contentSimilarity;
						} 
					}
				}
			};
		}
		try {
			Iterable<? extends EObject> typedFilter = Iterables.filter(entries, object.getClass());
			for(EObject entry : typedFilter) {
				boolean similar = checker.isSimilar(object, entry);
				if(similar) {
					return entry;
				}
			}
		} catch(FactoryException exception) {
			RefactoringPlugin.log(exception.getMessage(), exception);
		}
		return null;
	}
	
	public EObject findEntry(final String name, final EClass type, List<EObject> entries) {
		if(!entries.isEmpty()) {
			EObject entry = entries.get(0);
			final IQualifiedNameProvider nameProvider = extensions.getInstance(IQualifiedNameProvider.class, entry);
			Iterator<EObject> typedAndNamed = Iterables.filter(entries, new Predicate<EObject>() {
				public boolean apply(EObject object) {
					QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(object);
					return qualifiedName == null ? false : qualifiedName.getLastSegment().equals(name) && object.eClass() == type;
				}
			}).iterator();
			if(typedAndNamed.hasNext()) {
				return typedAndNamed.next();
			}
		}
		return null;
	}

	public boolean equallyTyped(EObject first, EObject second) {
		if(first == null && second != null) {
			return Boolean.FALSE;
		} else if(second == null && first != null) {
			return Boolean.FALSE;
		} else if(first == null && second == null) {
			return Boolean.TRUE;			
		} else {
			return first.eClass() == second.eClass();
		}
	}
	
	public boolean equallyNamed(EObject first, EObject second) {
		if(first == null || second == null) {
			return Boolean.FALSE;
		} else {
			IQualifiedNameProvider nameProvider = extensions.getInstance(IQualifiedNameProvider.class, first);
			QualifiedName firstName = nameProvider.getFullyQualifiedName(first);
			QualifiedName secondName = nameProvider.getFullyQualifiedName(second);
			if(firstName == null || secondName == null) {
				return Boolean.FALSE;
			} else {
				return firstName.getLastSegment().equals(secondName.getLastSegment());
			}
		}
	}
	
	protected GenericMatchEngineToCheckerBridge getMatchEngineBridge(EObject object) {
		final IQualifiedNameProvider nameProvider = extensions.getInstance(IQualifiedNameProvider.class, object);
		return new GenericMatchEngineToCheckerBridge() {
			@Override
			public double nameSimilarity(EObject first, EObject second) {
				if(nameProvider == null) {
					return NOT_MATCHING;
				}
				QualifiedName qualifiedNameFirst = nameProvider.getFullyQualifiedName(first);
				QualifiedName qulifiedNameSecond = nameProvider.getFullyQualifiedName(second);
				if(qualifiedNameFirst == null || qulifiedNameSecond == null) {
					return NOT_MATCHING;
				} else {
					String firstLastSegment = qualifiedNameFirst.getLastSegment();
					if(firstLastSegment == null) {
						return NOT_MATCHING;
					}
					String secondLastSegment = qulifiedNameSecond.getLastSegment();
					return firstLastSegment.equals(secondLastSegment) ? MATCHING : NOT_MATCHING;
				}
			}
			
			@Override
			public double contentSimilarity(EObject first, EObject second) throws FactoryException {
				return compareEObjects(first, second);
			}
			
			private double compareEObjects(EObject first, EObject second) throws FactoryException {
				List<EObject> firstParts = Lists.newArrayList(first.eContents());
				List<EObject> secondParts = Lists.newArrayList(second.eContents());
				if(firstParts.isEmpty()) {
					firstParts = Lists.newArrayList(first.eCrossReferences());
					secondParts = Lists.newArrayList(second.eCrossReferences());
				}
				if(firstParts.isEmpty()) {
					firstParts = Lists.newArrayList(first);
					secondParts = Lists.newArrayList(second);
				}
				double compareLists = compareLists(firstParts, secondParts);
				return compareLists;
			}
			
			private double compareLists(List<EObject> first, List<EObject> second) throws FactoryException {
				int firstSize = first.size();
				int secondSize = second.size();
				if(firstSize != secondSize) {
					return NOT_MATCHING;
				} else if(firstSize == 1) {
					EObject firstEntry = first.get(0);
					EObject secondEntry = second.get(0);
					return EcoreUtil.equals(firstEntry, secondEntry) ? MATCHING : NOT_MATCHING;
				} else {
					double similarity = NOT_MATCHING;
					for(int i=0; i<firstSize; i++) {
						EObject firstEntry = first.get(i);
						EObject secondEntry = second.get(i);
						similarity += compareEObjects(firstEntry, secondEntry);
					}
					return similarity / firstSize;
				}
			}
		};
	}
	
	protected MetamodelFilter getMetamodelFilter(EObject object) {
		MetamodelFilter filter = extensions.getInstance(MetamodelFilter.class, object);
		if(filter == null) {
			filter = new MetamodelFilter();
		}
		return filter;
	}
}
