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

import org.eclipse.emf.compare.FactoryException;
import org.eclipse.emf.compare.match.engine.internal.GenericMatchEngineToCheckerBridge;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class RefactoringMatchEngine extends GenericMatchEngineToCheckerBridge {

	public static final double SIMILAR = 1.0;
	public static final double THRESHOLD_0_9 = 0.9;
	public static final double THRESHOLD_0_7 = 0.7;
	public static final double THRESHOLD_0_5 = 0.5;
	public static final double THRESHOLD_0_2 = 0.2;
	public static final double DIFFERENT = 0.0;
	
	private Extensions extensions;
	
	@Inject
	public RefactoringMatchEngine(Extensions extensions) {
		this.extensions = extensions;
	}
	
	@Override
	public double nameSimilarity(EObject first, EObject second) {
		IQualifiedNameProvider nameProvider = extensions.getInstance(IQualifiedNameProvider.class, first);
		if(nameProvider == null) {
			return DIFFERENT;
		}
		QualifiedName firstName = nameProvider.getFullyQualifiedName(first);
		QualifiedName secondName = nameProvider.getFullyQualifiedName(second);
		if(firstName == null || secondName == null) {
			return DIFFERENT;
		} 
		String firstSegment = firstName.getLastSegment();
		String secondSegment = secondName.getLastSegment();
		if(firstSegment == null || secondSegment == null) {
			return DIFFERENT;
		}
		return firstSegment.equals(secondSegment) ? SIMILAR : DIFFERENT;
	}
	
	@Override
	public double contentSimilarity(EObject first, EObject second) throws FactoryException {
		return compare(first, second);
	}

	public double compare(EObject first, EObject second) throws FactoryException {
		Iterable<EObject> firstParts = IteratorExtensions.toIterable(first.eAllContents());
		Iterable<EObject> secondParts = IteratorExtensions.toIterable(second.eAllContents());
		if(Iterables.isEmpty(firstParts)) {
			firstParts = Lists.newArrayList(first.eCrossReferences());
			secondParts = Lists.newArrayList(second.eCrossReferences());
		}
		if(Iterables.isEmpty(firstParts)) {
			firstParts = Lists.newArrayList(first);
			secondParts = Lists.newArrayList(second);
		}
		return compare(firstParts, secondParts);
	}
	
	public double compare(Iterable<EObject> first, Iterable<EObject> second) throws FactoryException {
		int firstSize = Iterables.size(first);
		int secondSize = Iterables.size(second);
		if(firstSize != secondSize) {
			return DIFFERENT;
		} 
		Iterator<EObject> firstIterator = first.iterator();
		Iterator<EObject> secondIterator = second.iterator();
		if(firstSize > 1) {
			double similarity = DIFFERENT;
			while(firstIterator.hasNext() && secondIterator.hasNext()) {
				similarity += compare(firstIterator.next(), secondIterator.next());
			}
			return similarity / firstSize;
		}
		return EcoreUtil.equals(firstIterator.next(), secondIterator.next()) ? SIMILAR : DIFFERENT;
	}
}
