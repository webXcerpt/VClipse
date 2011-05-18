package org.vclipse.idoc.resource;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionManager;
import org.eclipse.xtext.util.IResourceScopeCache;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class IDocResourceDescriptionManager extends
		DefaultResourceDescriptionManager {

	@Inject
	private IQualifiedNameProvider nameProvider;

	@Inject
	private final IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;

	private static final String CACHE_KEY = IDocResourceDescriptionManager.class
			.getName() + "#getResourceDescription";

	@Override
	public IResourceDescription getResourceDescription(final Resource resource) {
		return cache.get(CACHE_KEY, resource,
				new Provider<IResourceDescription>() {
					public IResourceDescription get() {
						return new IDocResourceDescription(resource,
								nameProvider);
					}
				});
	}
}
