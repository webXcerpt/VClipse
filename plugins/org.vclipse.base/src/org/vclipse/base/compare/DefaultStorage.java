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
package org.vclipse.base.compare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.serializer.ISerializer;

import com.google.common.base.Charsets;

class DefaultStorage implements IEncodedStorage {

	protected static final String DEFAULT_NAME = "absent";
	protected static final String DEFAULT_CONTENT = "";
	protected static final String DEFAULT_ENCODING = Charsets.UTF_8.name();
	
	protected ISerializer serializer;
	protected IQualifiedNameProvider nameProvider;
	
	public DefaultStorage() {
		
	}
	
	public DefaultStorage(ISerializer serializer, IQualifiedNameProvider nameProvider) {
		if(serializer == null) {
			throw new IllegalArgumentException("Serializer is null.");
		}
		this.serializer = serializer;
		this.nameProvider = nameProvider;
	}
	
	@Override
	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(DEFAULT_CONTENT.getBytes());
	}
	
	@Override
	public String getCharset() throws CoreException {
		return DEFAULT_ENCODING;
	}
	
	@Override
	public String getName() {
		return DEFAULT_NAME;
	}

	@Override
	public IPath getFullPath() {
		return null;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}
}
