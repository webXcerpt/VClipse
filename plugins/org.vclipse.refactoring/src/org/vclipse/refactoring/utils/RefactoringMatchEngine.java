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

import java.util.List;

import org.eclipse.emf.compare.FactoryException;
import org.eclipse.emf.compare.match.engine.internal.GenericMatchEngineToCheckerBridge;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;

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
		List<EObject> firstParts = Lists.newArrayList(first.eAllContents());
		List<EObject> secondParts = Lists.newArrayList(second.eAllContents());
		if(firstParts.isEmpty()) {
			firstParts = Lists.newArrayList(first.eCrossReferences());
			secondParts = Lists.newArrayList(second.eCrossReferences());
		}
		if(firstParts.isEmpty()) {
			firstParts = Lists.newArrayList(first);
			secondParts = Lists.newArrayList(second);
		}
		return compare(firstParts, secondParts);
	}
	
	public double compare(List<EObject> first, List<EObject> second) throws FactoryException {
		int firstSize = first.size();
		int secondSize = second.size();
		if(firstSize != secondSize) {
			return DIFFERENT;
		} 
		if(firstSize > 1) {
			double similarity = DIFFERENT;
			for(int i=0; i<firstSize; i++) {
				similarity += compare(first.get(i), second.get(i));
			}
			return similarity / firstSize;
		}
		return EcoreUtil.equals(first.get(0), second.get(0)) ? SIMILAR : DIFFERENT;
	}
}
