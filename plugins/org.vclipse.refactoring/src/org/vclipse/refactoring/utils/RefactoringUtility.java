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
	
	public EObject findEntry(EObject object, List<EObject> entries) {
		IQualifiedNameProvider nameProvider = getInstance(IQualifiedNameProvider.class, object);
		
		// search by name and type
		EObject existingEntry = null;
		EClass eclass = object.eClass();
		QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(object);
		if(qualifiedName == null) {
			Iterator<EObject> iterator = getEntry(eclass, entries).iterator();
			if(iterator.hasNext()) {
				existingEntry = iterator.next();
			}
		} else {
			String segment = qualifiedName.getLastSegment();
			existingEntry = findEntry(segment, eclass, entries);
		}

		// search by type and container type
		if(existingEntry == null) {
			Iterator<EObject> iterator = getEntry(eclass, entries).iterator();
			while(iterator.hasNext()) {
				EObject next = iterator.next();
				if(equalTypeWithContainerType(next, object)) {
					existingEntry = next;
					break;
				}
			}								
		}
		return existingEntry;
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
	
	public boolean equalTypeWithContainerType(EObject first, EObject second) {
		EClass firstType = first.eClass();
		EClass secondType = second.eClass();
		if(first.eContainer() == null || second.eContainer() == null) {
			return firstType == secondType;
		}
		EClass firstContainerType = first.eContainer().eClass();
		EClass secondContainerType = second.eContainer().eClass();
		return firstType == secondType && firstContainerType == secondContainerType;
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
