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
package org.vclipse.refactoring.compare;

import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.serializer.ISerializer;

public class MultipleEntriesTypedElement extends DefaultElement {

	private final EObject[] elements;
	private final ISerializer serializer;
	private final IQualifiedNameProvider nameProvider;

	public static MultipleEntriesTypedElement getDefault() {
		return new MultipleEntriesTypedElement();
	}
	
	public MultipleEntriesTypedElement(ISerializer serializer, EObject ... elements) {
		this(serializer, null, elements);
	}
	
	public MultipleEntriesTypedElement(ISerializer serializer, IQualifiedNameProvider nameProvider, EObject ... elements) {
		super(serializer, nameProvider);
		this.elements = elements;
		this.serializer = serializer;
		this.nameProvider = nameProvider;
	}
	
	private MultipleEntriesTypedElement() {
		elements = null;
		serializer = null;
		nameProvider = null;
	}

	@Override
	public InputStream getContents() throws CoreException {
		if(serializer == null) {
			return super.getContents();
		} else if(buffer == null) {
			buffer = new MultipleEntriesStorage(serializer, nameProvider, elements);
		}
		return buffer.getContents();
	}

	@Override
	public String getName() {
		if(buffer == null) {
			if(serializer == null || elements.length == 0) {
				return super.getName();
			}
			buffer = new MultipleEntriesStorage(serializer, nameProvider, elements);
		}
		return buffer.getName();
	}
}
