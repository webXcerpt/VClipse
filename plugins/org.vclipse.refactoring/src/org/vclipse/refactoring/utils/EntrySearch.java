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

import org.eclipse.emf.common.util.EList;
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

	private Extensions extensions;
	
	@Inject
	public EntrySearch(Extensions extensions) {
		this.extensions = extensions;
	}
	
	public List<EObject> getContents(EObject object) {
		EObject rootContainer = EcoreUtil.getRootContainer(object);
		List<EObject> entries = Lists.newArrayList(object.eAllContents());
		entries.add(0, rootContainer);
		return entries;
	}
	
	@SuppressWarnings("unchecked")
	public EObject findEntry(EObject object, List<EObject> entries) {
		IQualifiedNameProvider nameProvider = extensions.getInstance(IQualifiedNameProvider.class, object);
		if(nameProvider != null) {
			QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(object);
			EClass searchForType = object.eClass();
			if(qualifiedName == null) {
				Iterable<EObject> iterable = getEntries(searchForType, entries);
				if(iterable == null || !iterable.iterator().hasNext()) {
					EObject container = object.eContainer();
					if(container == null) {
						return null;
					} else {
						EObject containerEntry = findEntry(container, entries);
						if(containerEntry == null) {
							return null;
						}
						Object value = containerEntry.eGet(object.eContainmentFeature());
						EObject entry = value instanceof EObject ? (EObject)value : null;
						return entry;
					}
				}
				EObject entry = iterable.iterator().next();
				return entry;
			} else {
				String searchForName = qualifiedName.getLastSegment();
				EObject entry = findEntry(searchForName, searchForType, entries);
				if(entry == null) {
					EObject container = object.eContainer();
					EObject containerEntry = findEntry(container, entries);
					if(containerEntry == null) {
						return null;
					}
					Object value = containerEntry.eGet(object.eContainmentFeature());
					if(value instanceof EObject) {
						entry = (EObject)value;
						return entry;
					} else {
						EList<EObject> valueEntries = (EList<EObject>)value;
						entry = findEntry(searchForName, searchForType, valueEntries);
						return entry;
					}
				} else {
					if(equallyTypedContainer(object, entry)) {
						EObject container = entry.eContainer();
						if(sameContainer(object, entry)) {
							return entry;
						}
						QualifiedName containerQualifiedName = nameProvider.getFullyQualifiedName(container);
						if(containerQualifiedName == null) {
							return entry;
						} else {
							return findNextEntry(object, entries, entry);
						}
					} else {
						return findNextEntry(object, entries, entry);
					}
				} 
			}
		}
		return null;
	}

	public EObject findEntry(String name, EClass type, List<EObject> entries) {
		if(type == null) {
			if(name == null) {
				return null;
			} else {
				Iterator<EObject> namedResults = getEntries(name, entries).iterator();
				return namedResults.hasNext() ? namedResults.next() : null;
			}
		} else {
			Iterator<EObject> iterator = getEntries(type, entries).iterator();
			if(name == null) {
				return iterator == null ? null : iterator.hasNext() ? iterator.next() : null;
			} else {
				Iterable<EObject> iterable = getEntries(name, Lists.newArrayList(iterator));
				if(iterable == null) {
					return null;
				}
				iterator = iterable.iterator();
				return iterator.hasNext() ? iterator.next() : null;
			}
		}
	}
	
	public Iterable<EObject> getEntries(final String name, Iterable<EObject> entries) {
		Iterator<EObject> iterator = entries.iterator();
		if(!iterator.hasNext() || name == null || name.isEmpty()) {
			return null;
		}
		final IQualifiedNameProvider nameProvider = extensions.getInstance(IQualifiedNameProvider.class, iterator.next());
		if(nameProvider != null) {
			return Iterables.filter(entries, new Predicate<EObject>() {
				public boolean apply(EObject eobject) {
					QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(eobject);
					return qualifiedName == null ? false : qualifiedName.getLastSegment().equals(name);
				}
			});			
		}
		return Lists.newArrayList();
	}
	
	public Iterable<EObject> getEntries(final EClass type, Iterable<EObject> entries) {
		Iterator<EObject> iterator = entries.iterator();
		if(!iterator.hasNext() || type == null) {
			return null;
		}
		return Iterables.filter(entries, new Predicate<EObject>() {
			public boolean apply(EObject eobject) {
				return eobject.eClass() == type;
			}
		});
	}
	
	public boolean sameContainer(EObject first, EObject second) {
		return equallyTypedContainer(first, second) && equallyNamedContainer(first, second);
	}
	
	public boolean equallyNamedContainer(EObject first, EObject second) {
		if(first == null || second == null) {
			return false;
		} else {
			EObject firstContainer = first.eContainer();
			EObject secondContainer = second.eContainer();
			if(firstContainer == null || secondContainer == null) {
				return false;
			} else {
				IQualifiedNameProvider nameProvider = extensions.getInstance(IQualifiedNameProvider.class, firstContainer);
				QualifiedName firstName = nameProvider.getFullyQualifiedName(firstContainer);
				QualifiedName secondName = nameProvider.getFullyQualifiedName(secondContainer);
				if(firstName == null || secondName == null) {
					return false;
				} else {
					return firstName.getLastSegment().equals(secondName.getLastSegment());
				}
			}
		}
	}
	
	public boolean equallyTypedContainer(EObject first, EObject second) {
		if(first == null || second == null) {
			return false;
		} else {
			EObject firstContainer = first.eContainer();
			EObject secondContainer = second.eContainer();
			if(firstContainer == null || secondContainer == null) {
				return false;
			} else {
				EClass firstContainerType = firstContainer.eClass();
				EClass secondContainerType = secondContainer.eClass();
				return firstContainerType == secondContainerType;
			}
		}
	}
	
	public boolean equallyTyped(EObject first, EObject second) {
		if(first == null || second == null) {
			return Boolean.FALSE;
		} else {
			EClass typefirst = first.eClass();
			EClass typesecond = second.eClass();
			return typefirst == typesecond;
		}
	}
	
	public boolean equallyNamed(EObject first, EObject second) {
		if(first == null || second == null) {
			return Boolean.FALSE;
		} else {
			IQualifiedNameProvider nameProvider = extensions.getInstance(IQualifiedNameProvider.class, first);
			QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(first);
			QualifiedName qualifiedName2 = nameProvider.getFullyQualifiedName(second);
			if(qualifiedName == null || qualifiedName2 == null) {
				return Boolean.FALSE;
			} else {
				return qualifiedName.getLastSegment().equals(qualifiedName2.getLastSegment());
			}
		}
	}
	
	protected EObject findNextEntry(EObject targetObject, List<EObject> entries, EObject previousEntry) {
		List<EObject> entriesCopy = Lists.newArrayList(entries);
		entriesCopy.remove(previousEntry);
		EObject findEntry = findEntry(targetObject, entriesCopy);
		return findEntry;
	}
}
