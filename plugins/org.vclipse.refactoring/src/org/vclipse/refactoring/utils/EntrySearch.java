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

import org.eclipse.emf.compare.match.IEqualityHelperFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class EntrySearch {

	private final Extensions extensions;
	
	private boolean refactoringExecuted;
	
	@Inject
	public EntrySearch(Extensions extensions) {
		this.extensions = extensions;
	}
	
	public void refactoringConditions(boolean enable) {
		refactoringExecuted = enable;
	}
	
	public List<EObject> getRootContents(EObject object) {
		EObject rootContainer = EcoreUtil.getRootContainer(object);
		List<EObject> entries = Lists.newArrayList(rootContainer.eAllContents());
		entries.add(0, rootContainer);
		return entries;
	}
	
	public EObject findEntry(EObject object, Iterable<EObject> entries) {
		Iterable<? extends EObject> typedFilter = Iterables.filter(entries, object.getClass());
		for(EObject entry : typedFilter) {
			boolean similar = similar(entry, object);
			if(similar) {
				return entry;
			}
		}
		return null;
	}
	
	public <T extends EObject> T findEntry(final String name, final EClass type, Iterable<T> entries) {
		Iterator<T> iterator = entries.iterator();
		if(iterator.hasNext()) {
			EObject entry = iterator.next();
			final IQualifiedNameProvider nameProvider = extensions.getInstance(IQualifiedNameProvider.class, entry);
			if(nameProvider != null) {
				Iterator<T> typedAndNamed = Iterables.filter(entries, new Predicate<T>() {
					public boolean apply(T object) {
						QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(object);
						return qualifiedName == null ? false : qualifiedName.getLastSegment().equals(name) && object.eClass() == type;
					}
				}).iterator();
				if(typedAndNamed.hasNext()) {
					return typedAndNamed.next();
				}
			}
		}
		return null;
	}
	
	public Iterable<EObject> findEntries(EObject object) {
		List<EObject> foundEntries = Lists.newArrayList();
		Iterable<EObject> entries = getRootContents(object);
		Iterable<? extends EObject> typedFilter = Iterables.filter(entries, object.getClass());
		for(EObject entry : typedFilter) {
			boolean similar = similar(entry, object);
			if(similar) {
				foundEntries.add(entry);
			}
		}
		return foundEntries;
	}
	
	public boolean equallyTyped(EObject first, EObject second) {
		if(first == null && second == null) {
			return true;
		} 
		if(first == null && second != null || first != null && second == null) {
			return false;
		} 
		return first.eClass() == second.eClass();
	}
	
	public boolean equallyNamed(EObject first, EObject second) {
		if(equallyTyped(first, second)) {
			IQualifiedNameProvider nameProvider = extensions.getInstance(IQualifiedNameProvider.class, first);
			QualifiedName firstName = nameProvider.getFullyQualifiedName(first);
			QualifiedName secondName = nameProvider.getFullyQualifiedName(second);
			if(firstName != null && secondName != null) {
				return firstName.getLastSegment().equals(secondName.getLastSegment());
			}
		}
		return false;
	}
	
	protected boolean similar(EObject first, EObject second) {
		if(refactoringExecuted) {
			IEqualityHelperFactory equalityHelperFactory = extensions.getInstance(IEqualityHelperFactory.class);
			return equalityHelperFactory.createEqualityHelper().matchingValues(first, second);
		}
		return EcoreUtil.equals(first, second);
	}
}
