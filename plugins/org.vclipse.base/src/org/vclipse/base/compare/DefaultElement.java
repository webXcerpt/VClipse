/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator(www.webXcerpt.com)
 ******************************************************************************/
package org.vclipse.base.compare;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.serializer.ISerializer;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;

class DefaultElement implements IDiffElement, IEncodedStreamContentAccessor, IAdaptable {

	protected ISerializer serializer;
	protected IQualifiedNameProvider nameProvider;
	protected IDiffContainer parentDiffContainer;
	protected IEncodedStorage buffer;
	
	protected Map<String, String> properties;
	
	public DefaultElement() {
		
	}
	
	public DefaultElement(ISerializer serializer, IQualifiedNameProvider nameProvider) {
		if(serializer == null) {
			throw new IllegalArgumentException("Serializer is null.");
		}
		this.serializer = serializer;
		this.nameProvider = nameProvider;
		properties = Maps.newHashMap();
	}
	
	@Override
	public String getName() {
		return DefaultStorage.DEFAULT_NAME;
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getType() {
		String value = properties.get("type");
		return value == null ? ITypedElement.UNKNOWN_TYPE : value;
	}

	@Override
	public InputStream getContents() throws CoreException {
		if(buffer == null) {
			buffer = new DefaultStorage();
		}
		return buffer.getContents();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public String getCharset() throws CoreException {
		return Charsets.UTF_8.name();
	}

	@Override
	public void setParent(IDiffContainer parent) {
		parentDiffContainer = parent;
	}
	
	@Override
	public IDiffContainer getParent() {
		return parentDiffContainer;
	}
	
	@Override
	public int getKind() {
		return Differencer.CHANGE;
	}
}
