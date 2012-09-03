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
package org.vclipse.refactoring.core;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public abstract class MethodCollector {

	protected Multimap<String, Method> name2Method;
	
	public class Pair<First, Second> {
		First first;
		Second second;
		Pair(First first, Second second) {
			this.first = first;
			this.second = second;
		}
		
		public First getFirst() {
			return first;
		}
		
		public Second getSecond() {
			return second;
		}
	}
	
	public MethodCollector() {
		name2Method = HashMultimap.create();
	}
	
	protected void collect(int paramCount) {
		for(Method method : getClass().getMethods()) {
			if(method.getParameterTypes().length == paramCount) {
				name2Method.put(method.getName(), method);
			}
		}
	}
	
	protected void collect(int paramCount, Class<?> ... paramTypes) {
		if(paramCount != paramTypes.length) {
			throw new IllegalArgumentException("paramCount should be equalt to paramTypes length");
		} else {
			List<Class<?>> requiredSignature = Lists.newArrayList(paramTypes);
			for(Method method : getClass().getMethods()) {
				List<Class<?>> existingSignature = Lists.newArrayList(method.getParameterTypes());
				if(equal(requiredSignature, existingSignature)) {
					name2Method.put(method.getName(), method);
				}
			}
		}
	}
	
	protected void collect(int paramCount, List<Integer> indexes, Class<?> ... types) {
		if(indexes.size() != types.length) {
			throw new IllegalArgumentException("The size of idexes list should be equal to the types length");
		} else {
			for(Method method : getClass().getMethods()) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				if(parameterTypes.length == paramCount) {
					List<Class<?>> existingSignature = Lists.newArrayList();
					List<Class<?>> requiredSignature = Lists.newArrayList();
					for(int index : indexes) {
						existingSignature.add(parameterTypes[index]);
						requiredSignature.add(types[index]);
					}
					if(equal(existingSignature, requiredSignature)) {
						name2Method.put(method.getName(), method);
					}
				}
			}
		}
	}
	
	protected Pair<EObject, Method> getMethod(EObject object, EStructuralFeature feature, String prefix) {
		if(object == null) {
			return null;
		} else {
			String prefixWithType = prefix + "_" + object.eClass().getName();
			Pair<EObject, Method> pair = iterateMethodCollection(object, prefixWithType);
			if(pair != null) {
				return pair;
			}
			
			String methodname = prefixWithType + (feature == null ? "" : "_" + feature.getName());
			pair = iterateMethodCollection(object, methodname);
			if(pair != null) {
				return pair;
			}
			
			feature = object.eContainingFeature();
			if(feature != null) {
				String nameWithContainingFeature = prefixWithType + "_" + feature.getName();
				pair = iterateMethodCollection(object, nameWithContainingFeature);
				if(pair != null) {
					return pair;
				}
			}
			
			for(EReference reference : object.eClass().getEAllReferences()) {
				String nameWithFeature = prefixWithType + "_" + reference.getName();
				pair = iterateMethodCollection(object, nameWithFeature);
				if(pair != null) {
					return pair;
				}
			}
			
			for(EReference reference : object.eClass().getEAllReferences()) {
				String nameWithFeature = prefix + "_" + reference.getName();
				pair = iterateMethodCollection(object, nameWithFeature);
				if(pair != null) {
					return pair;
				}
			}
			return getMethod(object.eContainer(), feature, prefix);
		}
	}

	protected Pair<EObject, Method> iterateMethodCollection(EObject object, String methodname) {
		Iterator<Method> iterator = name2Method.get(methodname).iterator();
		while(iterator.hasNext()) {
			Method current = iterator.next();
			return new Pair<EObject, Method>(object, current);
		}
		return null;
	}

	private boolean equal(List<Class<?>> signature_one, List<Class<?>> signature_two) {
		if(signature_one.size() != signature_two.size()) {
			return false;
		} else {
			for(int i=0; i<signature_one.size(); i++) {
				if(signature_one.get(i) != signature_two.get(i)) {
					return false;
				}
			}
		}
		return true;
	}
}
