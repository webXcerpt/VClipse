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
	
	public <T> T getInstance(EObject object, Class<T> type) {
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
			IQualifiedNameProvider nameProvider = getInstance(object, IQualifiedNameProvider.class);
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
	
	public EObject getEntry(List<EObject> entries, String name, EClass type) {
		if(type == null) {
			if(name == null) {
				return null;
			} else {
				Iterator<EObject> namedResults = getEntry(entries, name);
				return namedResults.hasNext() ? namedResults.next() : null;
			}
		} else {
			Iterator<EObject> iterator = getEntry(entries, type).iterator();
			if(name == null) {
				return iterator == null ? null : iterator.hasNext() ? iterator.next() : null;
			} else {
				iterator = getEntry(Lists.newArrayList(iterator), name);
				return iterator == null ? null : iterator.hasNext() ? iterator.next() : null;
			}
		}
	}
	
	public Iterator<EObject> getEntry(Iterable<EObject> entries, final String name) {
		Iterator<EObject> iterator = entries.iterator();
		if(!iterator.hasNext() || name == null || name.isEmpty()) {
			return null;
		}
		final IQualifiedNameProvider nameProvider = getInstance(iterator.next(), IQualifiedNameProvider.class);
		if(nameProvider != null) {
			return Iterables.filter(entries, new Predicate<EObject>() {
				public boolean apply(EObject eobject) {
					QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(eobject);
					return qualifiedName == null ? false : qualifiedName.getLastSegment().equals(name);
				}
			}).iterator();			
		}
		return null;
	}
	
	public Iterable<EObject> getEntry(Iterable<EObject> entries, EClass type) {
		Iterator<EObject> iterator = entries.iterator();
		if(!iterator.hasNext() || type == null) {
			return null;
		}
		List<EObject> foundEntries = Lists.newArrayList();
		for(EObject entry : entries) {
			if(entry.eClass() == type) {
				foundEntries.add(entry);
			}
		}
		return foundEntries;
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
