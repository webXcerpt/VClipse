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
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.vclipse.base.VClipseStrings;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.configuration.ConfigurationProvider;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class RefactoringUtility {

	@Inject
	private ConfigurationProvider configuration;
	
	public Injector getInjector(EObject object) {
		if(object == null) {
			return null;
		}
		EObject container = EcoreUtil2.getRootContainer(object);
		if(container == null) {
			return null;
		}
		Injector injector = configuration.getInjector().get(container.eClass());
		return injector;
	}
	
	public <T> T getInstance(Class<T> type, EObject object) {
		try {
			Injector injector = getInjector(object);
			return injector == null ? null : injector.getInstance(type);
		} catch(ConfigurationException exception) {
			RefactoringPlugin.log(exception.getMessage(), exception);
			return null;
		}
	}
	
	public Set<String> getText(List<EObject> values) {
		Set<String> names = Sets.newHashSet();
		if(!values.isEmpty()) {
			EObject object = values.get(0);
			IQualifiedNameProvider nameProvider = getInstance(IQualifiedNameProvider.class, object);
			if(nameProvider != null) {
				for(EObject value : values) {
					QualifiedName qualifiedName = nameProvider.apply(value);
					if(qualifiedName != null) {
						String name = qualifiedName.getLastSegment();
						if(name != null) {
							names.add(name);						
						}						
					}
				}
			}
		}
		return names;
	}
	
	public String getRefactoringText(IRefactoringUIContext context) {
		String text = context.getLabel();
		if(text == null || text.isEmpty()) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(context.getType().name() + " ");
			if(context.getStructuralFeature() != null) {
				appendToBuffer(buffer, context.getStructuralFeature().getName(), true);
			}
			appendToBuffer(buffer, context.getSourceElement().eClass().getName(), false);
			return buffer.toString();
		}
		return text;
	}
	
	@SuppressWarnings("unchecked")
	public EObject findEntry(EObject object, List<EObject> entries) {
		IQualifiedNameProvider nameProvider = getInstance(IQualifiedNameProvider.class, object);
		QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(object);
		EClass searchForType = object.eClass();
		if(qualifiedName == null) {
			EObject container = object.eContainer();
			if(container == null) {
				EObject entry = getEntry(searchForType, entries).iterator().next();
				return entry;
			} else {
				EObject containerEntry = findEntry(container, entries);
				Object value = containerEntry.eGet(object.eContainmentFeature());
				EObject entry = value instanceof EObject ? (EObject)value : null;
				return entry;
			}
		} else {
			String searchForName = qualifiedName.getLastSegment();
			EObject entry = findEntry(searchForName, searchForType, entries);
			if(entry == null) {
				EObject container = object.eContainer();
				EObject containerEntry = findEntry(container, entries);
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

	private EObject findNextEntry(EObject object, List<EObject> entries, EObject entry) {
		List<EObject> entriesCopy = Lists.newArrayList(entries);
		entriesCopy.remove(entry);
		EObject findEntry = findEntry(object, entriesCopy);
		return findEntry;
	}
	
	public EObject findEntry(String name, EClass type, List<EObject> entries) {
		if(type == null) {
			if(name == null) {
				return null;
			} else {
				Iterator<EObject> namedResults = getEntry(name, entries).iterator();
				return namedResults.hasNext() ? namedResults.next() : null;
			}
		} else {
			Iterator<EObject> iterator = getEntry(type, entries).iterator();
			if(name == null) {
				return iterator == null ? null : iterator.hasNext() ? iterator.next() : null;
			} else {
				iterator = getEntry(name, Lists.newArrayList(iterator)).iterator();
				return iterator == null ? null : iterator.hasNext() ? iterator.next() : null;
			}
		}
	}
	
	public Iterable<EObject> getEntry(final String name, Iterable<EObject> entries) {
		Iterator<EObject> iterator = entries.iterator();
		if(!iterator.hasNext() || name == null || name.isEmpty()) {
			return null;
		}
		final IQualifiedNameProvider nameProvider = getInstance(IQualifiedNameProvider.class, iterator.next());
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
	
	public Iterable<EObject> getEntry(final EClass type, Iterable<EObject> entries) {
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
	
	@SuppressWarnings("unchecked")
	public EList<EObject> getEObjectList(List<?> elements) {
		return (EList<EObject>)elements;
	}
	
	public EList<EObject> copy(EList<? extends EObject> elements) {
		return new BasicEList<EObject>(EcoreUtil2.copyAll(elements));
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
				IQualifiedNameProvider nameProvider = getInstance(IQualifiedNameProvider.class, firstContainer);
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
	
	private void appendToBuffer(StringBuffer buffer, String text, boolean handleLastIndex) {
		List<String> parts = VClipseStrings.splitCamelCase(text);
		for(String part : parts) {
			buffer.append(part.toLowerCase());
			int indexOf = parts.indexOf(part);
			if(indexOf < parts.size()) {
				buffer.append(" ");
			}
			if(handleLastIndex && (indexOf == parts.size() - 1)) {
				buffer.append(" for ");
			}
		}
	}
}
