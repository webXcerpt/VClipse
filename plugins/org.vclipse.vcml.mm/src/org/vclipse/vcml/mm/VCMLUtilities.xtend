/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.vcml.mm

import com.google.common.collect.Lists
import java.util.Collections
import java.util.Comparator
import java.util.List
import org.eclipse.emf.ecore.EObject

/**
 * Utilities for VCML Objects.
 */
class VCMLUtilities {
	
	/**
	 * Sorts a list with a comparator.
	 */
	def <T extends EObject> sortEntries(List<T> entries, Comparator<T> comparator) {
		val entriesCopy = Lists::<T>newArrayList(entries)
		Collections::sort(entriesCopy, comparator)
		entries.clear
		entries.addAll(entriesCopy)
		return
	}
	
//	static public <T extends VCObject> Iterable<T> getObjectsByNameAndType(final String name, vcmlModel vcmlModel, final java.lang.Class<T> type) { 
//		if(vcmlModel == null || type == null) {
//			return Lists.newArrayList();
//		} else {
//			Iterable<T> typeFilter = Iterables.filter(vcmlModel.getObjects(), type);
//			if(name == null || name.isEmpty()) {
//				return typeFilter;
//			} else {
//				return Iterables.filter(typeFilter, new Predicate<VCObject>() {
//					public boolean apply(VCObject object) {
//						return name.equals(object.getName()) && type.isAssignableFrom(object.getClass());
//					}
//				});
//			}
//		}
//	}
//	
//	/*
//	 * Searches for an entry with a given type and name in an iterable. Returns the first match, null if there is no match.
//	 */
//	public static <T extends EObject> T findEntry(final String name, final EClass type, final Iterable<T> entries, final IQualifiedNameProvider nameProvider) {
//		Iterator<T> iterator = entries.iterator();
//		if(iterator.hasNext()) {
//			if(nameProvider != null) {
//				Iterator<T> typedAndNamed = Iterables.filter(entries, new Predicate<T>() {
//					public boolean apply(T object) {
//						QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(object);
//						return qualifiedName == null ? false : qualifiedName.toString().equals(name) && object.eClass() == type;
//					}
//				}).iterator();
//				if(typedAndNamed.hasNext()) {
//					return typedAndNamed.next();
//				}
//			}
//		}
//		return null;
//	}
}