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
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.serializer.ISerializer;
import org.vclipse.base.VClipseStrings;
import org.vclipse.base.naming.INameProvider;
import org.vclipse.refactoring.ExtensionsReader;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class RefactoringUtility {

	@Inject
	private ExtensionsReader extensionReader;
	
	public Injector getInjector(EObject object) {
		if(object == null) {
			return null;
		}
		EObject container = EcoreUtil2.getRootContainer(object);
		if(container == null) {
			return null;
		}
		Iterator<Injector> iterator = extensionReader.getInjector().get(container.eClass()).iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}
	
	public INameProvider getNameProvider(EObject object) {
		Injector injector = getInjector(object);
		return injector == null ? null : injector.getInstance(INameProvider.class);
	}
	
	public EValidator.Registry getValidator(EObject object) {
		Injector injector = getInjector(object);
		return injector == null ? null : injector.getInstance(EValidator.Registry.class);
	}
	
	public ISerializer getSerializer(EObject object) {
		Injector injector = getInjector(object);
		return injector == null ? null : injector.getInstance(ISerializer.class);
	}
	
	public Set<String> getText(List<EObject> values) {
		Set<String> names = Sets.newHashSet();
		if(!values.isEmpty()) {
			EObject object = values.get(0);
			INameProvider nameProvider = getNameProvider(object);
			if(nameProvider != null) {
				for(EObject value : values) {
					String name = nameProvider.apply(value);
					if(name != null) {
						names.add(name);						
					}
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
				String result = nameProvider.apply(eobject);
				return result == null ? false : result.equals(name);
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
	
	public EObject rootContainerCopy(EObject object, String resourceNameExtension) {
		Resource resource = object.eResource();
		ResourceSet set = resource.getResourceSet();
		URI uri = URI.createURI("temporary_" + resourceNameExtension + "." + resource.getURI().fileExtension());
		try {
			resource = set.getResource(uri, true);
		} catch(Exception exception) {
			resource = set.getResource(uri, true);
		}
		resource.getContents().clear();
		EObject container = EcoreUtil.getRootContainer(object);
		EcoreUtil.Copier copier = new EcoreUtil.Copier(true);
		container = copier.copy(container);
		copier.copyReferences();
		resource.getContents().add(container);
		return container;
	}
	
	public EObject getEqualTo(EObject searchFor, EObject rootContainer) {
		if(equals(searchFor, rootContainer)) {
			return rootContainer;
		}
		TreeIterator<EObject> contents = rootContainer.eAllContents();
		while(contents.hasNext()) {
			EObject next = contents.next();
			if(equals(searchFor, next)) {
				return next;
			}
		}
		return null;
	}
	
	public boolean equals(EObject object_one, EObject object_two) {
		return EcoreUtil.equals(object_one, object_two) && EcoreUtil.equals(object_one.eContainer(), object_two.eContainer());
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
