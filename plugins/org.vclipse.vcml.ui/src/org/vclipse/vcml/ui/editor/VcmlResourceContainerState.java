package org.vclipse.vcml.ui.editor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.ui.containers.AbstractAllContainersState;
import org.vclipse.base.ui.util.VClipseResourceUtil;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.Model;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class VcmlResourceContainerState extends AbstractAllContainersState {
	
	public static final String VCML_EXTENSION = "." + DependencySourceUtils.EXTENSION_VCML;
	
	@Inject
	private DependencySourceUtils sourceUtils;
	
	@Inject
	private IWorkspace workspace;
	
	@Inject
	private VClipseResourceUtil resourceUtil;
	
	public List<String> getVisibleContainerHandles(String handle) {
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
					if(topObject instanceof Model) {
						for(Import immport : ((Model)topObject).getImports()) {
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

	public Collection<URI> getContainedURIs(String containerHandle) {
		return containerHandle.endsWith(VCML_EXTENSION) ? 
				Collections.singletonList(URI.createURI(containerHandle)) :
					super.getContainedURIs(containerHandle);
	}

	public boolean isEmpty(String containerHandle) {
		return false;
	}

	public String getContainerHandle(URI uri) {
		return sourceUtils.getVcmlResourceURI(uri).toString();
	}

	@Override
	protected String doInitHandle(URI uri) {
		return null;
	}

	@Override
	protected Collection<URI> doInitContainedURIs(String containerHandle) {
		if(containerHandle.contains(VCML_EXTENSION)) {
			containerHandle = containerHandle.replace(VCML_EXTENSION, DependencySourceUtils.SUFFIX_SOURCEFOLDER);
		}
		final List<URI> containedUris = Lists.newArrayList();
		String stringHandle = URI.createURI(containerHandle).toPlatformString(true);
		IResource findMember = workspace.getRoot().findMember(stringHandle);
		if(findMember instanceof IContainer) {
			try {
				((IContainer)findMember).accept(new IResourceVisitor() {
					public boolean visit(IResource resource) throws CoreException {
						if(resource instanceof IFile) {
							containedUris.add(resourceUtil.getResource(((IFile)resource)).getURI());
						}
						return true;
					}
				});
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return containedUris;
	}

	@Override
	protected List<String> doInitVisibleHandles(String handle) {
		return null;
	}
}