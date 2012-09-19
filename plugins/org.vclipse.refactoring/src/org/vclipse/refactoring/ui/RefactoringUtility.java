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

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.linking.ILinker;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.impl.ListBasedDiagnosticConsumer;
import org.eclipse.xtext.serializer.ISerializer;
import org.vclipse.base.VClipseStrings;
import org.vclipse.refactoring.ExtensionsReader;
import org.vclipse.refactoring.RefactoringPlugin;

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
	
	public EObject rootContainerCopy(EObject object) {
		EObject container = EcoreUtil.getRootContainer(object);
		ISerializer serializer = getInstance(container, ISerializer.class);
		IParser parser = getInstance(container, IParser.class);
		String string = serializer.serialize(container);		
		IParseResult parseResult = parser.parse(new StringReader(string));
		Resource resource = container.eResource();
		ResourceSet resourceSet = resource.getResourceSet();
		URI uri = resource.getURI();
		uri = URI.createURI(uri.toString() + ".preview." + uri.fileExtension());
		try {
			resource = resourceSet.getResource(uri, true);
		} catch(Exception exception) {
			resource = resourceSet.getResource(uri, true);
		}
		resource.getContents().clear();
		resource.getContents().add(parseResult.getRootASTElement());
		ILinker linker = getInstance(container, ILinker.class);
		final ListBasedDiagnosticConsumer consumer = new ListBasedDiagnosticConsumer();
		linker.linkModel(parseResult.getRootASTElement(), consumer);
		return parseResult.getRootASTElement();
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
