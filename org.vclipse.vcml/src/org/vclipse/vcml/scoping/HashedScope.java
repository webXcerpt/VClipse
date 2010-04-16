/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
package org.vclipse.vcml.scoping;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopedElement;
import org.eclipse.xtext.scoping.impl.SimpleScope;

public class HashedScope extends SimpleScope {

	private Map<String, IScopedElement> nameToElements;

	private Iterator<IScopedElement> newElements;
	
	public HashedScope(IScope outer, Iterable<IScopedElement> elements) {
		super(outer, elements);
	}

	public IScopedElement getScopedElementByName(String name) {
		if (name==null)
			throw new NullPointerException("name");
		if (nameToElements==null) {
			nameToElements = new HashMap<String, IScopedElement>();
			newElements = getContents().iterator();
		}
		IScopedElement hashedElement = nameToElements.get(name);
		if (hashedElement!=null) {
			return hashedElement;
		} else {
			if (newElements!=null) {
				while (newElements.hasNext()) {
					IScopedElement element = newElements.next();
					String elementName = element.name();
					nameToElements.put(elementName, element);
					if (name.equals(elementName)) {
						return element;
					}
				}
				newElements = null; // free reference to newElements
			}
			IScope outer = getOuterScope();
			if (outer!=null && outer!=IScope.NULLSCOPE) {
				if (outer instanceof HashedScope) {
					return ((HashedScope)outer).getScopedElementByName(name);
				} else {
					for (IScopedElement element : outer.getAllContents()) {
						if (name.equals(element.name())) {
							return element;
						}
					}
				}
			}
		}
		return null;
	}
	
}
