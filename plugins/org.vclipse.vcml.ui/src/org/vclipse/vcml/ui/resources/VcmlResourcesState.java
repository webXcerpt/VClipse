package org.vclipse.vcml.ui.resources;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.ui.containers.AbstractAllContainersState;
import org.vclipse.base.ui.BaseUiPlugin;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class VcmlResourcesState extends AbstractAllContainersState {
	
	public static final String VCML_EXTENSION = "." + DependencySourceUtils.EXTENSION_VCML;
	
	private final Set<String> EXTENSIONS = Sets.newHashSet(
			DependencySourceUtils.EXTENSION_CONSTRAINT, DependencySourceUtils.EXTENSION_PRECONDITION, 
				DependencySourceUtils.EXTENSION_PROCEDURE, DependencySourceUtils.EXTENSION_SELECTIONCONDITION);
	
	@Inject
	private DependencySourceUtils sourceUtils;
	
	@Inject
	private IWorkspace workspace;
	
	private Map<String, List<String>> cache_visibleContainerHandles;
	
	@Override
	protected void initialize() {
		System.err.println("initialize");
		super.initialize();
		cache_visibleContainerHandles = Maps.newHashMap();
	}
	
	@Override
	protected String doInitHandle(URI uri) {
		if(DependencySourceUtils.EXTENSION_VCML.equals(uri.fileExtension())) {
			return uri.toString();
		}
		return null;
	}
	
	@Override
	protected List<String> doInitVisibleHandles(String handle) {
		System.err.println("doInitVisibleHandles " + handle + " " + this);
		List<String> visibleContainerHandles = Lists.newArrayList(handle);
		if(handle.endsWith(VCML_EXTENSION)) {
			URI createURI = URI.createURI(handle);
			ResourceSetImpl resourceSetImpl = new ResourceSetImpl();
			Resource resource = null;
			try {
				resource = resourceSetImpl.getResource(createURI, true);
			} catch(Exception exception) {
				resource = resourceSetImpl.createResource(createURI);
			}
			if(resource != null) {
				EList<EObject> contents = resource.getContents();
				if(!contents.isEmpty()) {
					EObject topObject = contents.get(0);
					if(topObject instanceof VcmlModel) {
						VcmlModel vcmlModel = (VcmlModel)topObject;
						for(Import immport : vcmlModel.getImports()) {
							String[] parts = immport.getImportURI().split("/");
							URI importedUri = URI.createURI(handle).trimSegments(parts.length - 1);
							for(int i=1; i<parts.length; i++) {
								importedUri = importedUri.appendSegment(parts[i]);
							}
							visibleContainerHandles.add(importedUri.toString());
						}
					}
				}
			}
		}
		return visibleContainerHandles;
	}
	
	@Override
	public boolean isEmpty(String containerHandle) {
		return false;
	}
	
	@Override
	public List<String> getVisibleContainerHandles(String handle) {
		List<String> visibleContainerHandles = cache_visibleContainerHandles.get(handle);
		if(visibleContainerHandles == null) {
			visibleContainerHandles = doInitVisibleHandles(handle);
			cache_visibleContainerHandles.put(handle, visibleContainerHandles);
		}
		return visibleContainerHandles;
	}

	@Override
	protected Collection<URI> doInitContainedURIs(String containerHandle) {
		final List<URI> containedUris = Lists.newArrayList();
		if(containerHandle.contains(VCML_EXTENSION)) {
			containerHandle = containerHandle.replace(VCML_EXTENSION, DependencySourceUtils.SUFFIX_SOURCEFOLDER);
		}
		String stringHandle = URI.createURI(containerHandle).toPlatformString(true);
		if(stringHandle != null) {
			IResource findMember = workspace.getRoot().findMember(stringHandle);
			if(findMember instanceof IContainer) {
				try {
					((IContainer)findMember).accept(new IResourceVisitor() {
						public boolean visit(IResource resource) throws CoreException {
							if(resource instanceof IFile) {
								IFile file = (IFile)resource;
								if(EXTENSIONS.contains(file.getFileExtension())) {
									String path = file.getFullPath().toString();
									URI uri = URI.createPlatformResourceURI(path, true);
									containedUris.add(uri);									
								}
							}
							return true;
						}
					});
				} catch(CoreException exception) {
					BaseUiPlugin.log(exception.getMessage(), exception);
				}
			}
		}
		return containedUris;
	}
	
	public Collection<URI> getContainedURIs(String containerHandle) {
		return containerHandle.endsWith(DependencySourceUtils.EXTENSION_VCML) ? 
				Collections.singletonList(URI.createURI(containerHandle)) :
					super.getContainedURIs(containerHandle);
	}

	public String getContainerHandle(URI uri) {
		return sourceUtils.getVcmlResourceURI(uri).toString();
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		switch(event.getType()) {
			case IResourceChangeEvent.PRE_DELETE : case IResourceChangeEvent.PRE_CLOSE :
				IResource resource = event.getResource();
				if(resource instanceof IContainer) {
					IContainer container = (IContainer)resource;
					final List<String> vcmlcontainers = Lists.newArrayList();
					try {
						container.accept(new IResourceVisitor() {
							public boolean visit(IResource resource) throws CoreException {
								if(resource instanceof IFile && DependencySourceUtils.EXTENSION_VCML.equals(((IFile)resource).getFileExtension())) {
									vcmlcontainers.add(URI.createPlatformResourceURI(resource.getFullPath().toString(), true).toString());
								}
								return true;
							}
						});
					} catch(CoreException exception) {
						BaseUiPlugin.log(exception.getMessage(), exception);
					}
					for(String containerhandle : vcmlcontainers) {
						cache_visibleContainerHandles.remove(containerhandle);
					}
				}
				break;
			default	:
				
				// FIXME deactivate clear of the cache due to performance reasons (a re-parse of VCML files is required)
				// This has to be cleaned up!
				
				// cache_visibleContainerHandles.clear();
				super.resourceChanged(event);
		}
	}
}
