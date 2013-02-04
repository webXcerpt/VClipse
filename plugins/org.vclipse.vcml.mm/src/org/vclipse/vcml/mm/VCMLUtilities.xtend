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
import org.eclipse.emf.ecore.EClass
import org.vclipse.base.naming.INameProvider
import com.google.common.base.Strings
import com.google.common.collect.Iterables
import org.vclipse.vcml.vcml.Option
import org.eclipse.emf.common.util.EList
import org.vclipse.vcml.vcml.OptionType

/**
 * Utilities for VCML Objects.
 */
class VCMLUtilities {
	
	/**
	 * Returns an option with requested type, null if such an option does not exist as an entry.
	 */
	def Option getOption(EList<Option> options, OptionType type) {
		for(option : options) {
			if(option.name == type) {
				return option
			}
		}
		return null
	}
	
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
	
	/**
	 * Searches for an entry with a given type and name in entries. 
	 * Returns the first match, null if there is no match.
	 */
	def <T extends EObject> findEntry(String name, EClass type, Iterable<T> entries, INameProvider nameProvider) {
		if(nameProvider == null) {
			return null
		}
		val iterator = entries.iterator
		if(iterator.hasNext) {
			val typedAndNamed = Iterables::filter(entries, [
				T entry |
					val entryName = nameProvider.getName(entry)
					if(Strings::isNullOrEmpty(entryName))
						return false
					return entryName.equals(name) && entry.eClass == type
			]).iterator
			if(typedAndNamed.hasNext) {
				return typedAndNamed.next
			}
		}
		return null
	}
}