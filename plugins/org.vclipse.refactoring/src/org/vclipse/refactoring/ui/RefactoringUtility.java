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
package org.vclipse.refactoring.ui;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.vclipse.base.VClipseStrings;
import org.vclipse.base.naming.INameProvider;
import org.vclipse.refactoring.ExtensionsReader;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class RefactoringUtility {

	@Inject
	private ExtensionsReader extensionReader;
	
	public INameProvider getNameProvider(EObject object) {
		INameProvider nameProvider = null;
		if(object != null) {
			EObject container = EcoreUtil2.getRootContainer(object);
			Iterator<Injector> iterator = extensionReader.getInjector().get(container.eClass()).iterator();
			if(iterator.hasNext()) {
				nameProvider = iterator.next().getInstance(INameProvider.class);
			}
		}
		return nameProvider;
	}
	
	public Set<String> getText(List<EObject> values) {
		Set<String> names = Sets.newHashSet();
		if(!values.isEmpty()) {
			EObject object = values.get(0);
			INameProvider nameProvider = getNameProvider(object);
			if(nameProvider != null) {
				for(EObject value : values) {
					String name = nameProvider.apply(value);
					if(name == null) {
						System.err.println("name for " + value + " was null");
					}
					names.add(name);
				}
			}
		}
		return names;
	}
	
	public EObject get(EList<EObject> entries, String name, Class<? extends EObject> type) {
		if(type == null) {
			if(name == null) {
				return null;
			} else {
				Iterator<EObject> namedResults = get(entries, name);
				return namedResults.hasNext() ? namedResults.next() : null;
			}
		} else {
			Iterator<? extends EObject> iterator = get(entries, type);
			if(name == null) {
				return iterator.hasNext() ? iterator.next() : null;
			} else {
				iterator = get(Lists.newArrayList(iterator), name);
				return iterator.hasNext() ? iterator.next() : null;
			}
		}
	}
	
	public Iterator<EObject> get(Iterable<EObject> entries, final String name) {
		Iterator<EObject> iterator = entries.iterator();
		if(!iterator.hasNext() || name == null || name.isEmpty()) {
			return null;
		}
		final INameProvider nameProvider = getNameProvider(iterator.next());
		return Iterables.filter(entries, new Predicate<EObject>() {
			public boolean apply(EObject eobject) {
				return nameProvider.apply(eobject).equals(name);
			}
		}).iterator();
	}
	
	public Iterator<? extends EObject> get(Iterable<EObject> entries, Class<? extends EObject> type) {
		Iterator<EObject> iterator = entries.iterator();
		if(!iterator.hasNext() || type == null) {
			return null;
		}
		return Iterables.filter(entries, type).iterator();
	}
	
	public String getRefactoringText(IUIRefactoringContext context) {
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
