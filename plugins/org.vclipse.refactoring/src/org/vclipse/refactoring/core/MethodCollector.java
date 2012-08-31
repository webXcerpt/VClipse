package org.vclipse.refactoring.core;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

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
	
	public MethodCollector(int paramCount) {
		name2Method = HashMultimap.create(); 
		for(Method method : getClass().getMethods()) {
			if(method.getParameterTypes().length == paramCount) {
				name2Method.put(method.getName(), method);
			}
		}
	}
	
	protected Pair<EObject, Method> getMethod(EObject object, EStructuralFeature feature, String prefix) {
		if(object == null) {
			return null;
		} else {
			String prefixWithObject = prefix + "_" + object.eClass().getName();
			String methodname = prefixWithObject + (feature == null ? "" : "_" + feature.getName());
			Pair<EObject, Method> pair = iterateMethodCollection(object, methodname);
			if(pair != null) {
				return pair;
			}
			
			feature = object.eContainingFeature();
			if(feature != null) {
				String nameWithContainingFeature = prefixWithObject + "_" + feature.getName();
				pair = iterateMethodCollection(object, nameWithContainingFeature);
				if(pair != null) {
					return pair;
				}
			}
			
			for(EReference reference : object.eClass().getEAllReferences()) {
				String nameWithFeature = prefixWithObject + "_" + reference.getName();
				pair = iterateMethodCollection(object, nameWithFeature);
				if(pair != null) {
					return pair;
				}
			}
			return getMethod(object.eContainer(), feature, prefix);
		}
	}

	protected Pair<EObject, Method> iterateMethodCollection(EObject object, String methodname) {
		Class<?> instanceClass = object.eClass().getInstanceClass();
		Iterator<Method> iterator = name2Method.get(methodname).iterator();
		while(iterator.hasNext()) {
			Method current = iterator.next();
			Set<Class<?>> parameterSet = Sets.newHashSet(current.getParameterTypes());
			if(parameterSet.contains(instanceClass)) {
				return new Pair<EObject, Method>(object, current);
			}
		}
		return null;
	}
}
