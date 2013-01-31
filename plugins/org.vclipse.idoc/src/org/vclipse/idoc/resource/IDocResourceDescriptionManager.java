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
package org.vclipse.idoc.resource;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionManager;
import org.eclipse.xtext.util.IResourceScopeCache;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class IDocResourceDescriptionManager extends
		DefaultResourceDescriptionManager {

	@Inject
	private IDefaultResourceDescriptionStrategy strategy;

	@Inject
	private final IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;

	private static final String CACHE_KEY = IDocResourceDescriptionManager.class
			.getName() + "#getResourceDescription";

	@Override
	public IResourceDescription getResourceDescription(final Resource resource) {
		return cache.get(CACHE_KEY, resource,
				new Provider<IResourceDescription>() {
					public IResourceDescription get() {
						return new IDocResourceDescription(resource, strategy);
					}
				});
	}
}
