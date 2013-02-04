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

import com.google.common.base.Strings
import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import java.util.Collections
import java.util.Comparator
import java.util.List
import java.util.Map
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.vclipse.base.naming.INameProvider
import org.vclipse.vcml.vcml.Characteristic
import org.vclipse.vcml.vcml.CharacteristicValue
import org.vclipse.vcml.vcml.Option
import org.vclipse.vcml.vcml.OptionType
import org.vclipse.vcml.vcml.SymbolicType

/**
 * Utilities for VCML Objects.
 */
class VCMLUtilities {
	
	/**
	 * Returns an option with requested type, null if such an option does not exist as an entry.
	 */
	def Option getOption(List<Option> options, OptionType type) {
		for(option : options) {
			if(option.name == type) {
				return option
			}
		}
		return null
	}
	
	/**
	 * Returns a mapping name to value for a characteristic, only if its type is symbolic.
	 */
	def Map<String, CharacteristicValue> getCharacteristicValues(Characteristic cstic) {
		val name2Value = Maps::<String, CharacteristicValue>newHashMap
		val type = cstic.type
		if(type instanceof SymbolicType) {
			for(value : (type as SymbolicType).values) {
				name2Value.put(value.name, value)
			}	
		}
		return name2Value
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