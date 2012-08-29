package org.vclipse.base.ui.util;

import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.util.SimpleAttributeResolver;
import org.eclipse.xtext.util.StringInputStream;

public class EObjectStorage implements IStorage {

	private final EObject object;
	private final ISerializer prettyPrinter;
	
	public EObjectStorage(EObject object, ISerializer prettyPrinter) {
		if(object == null) {
			throw new IllegalArgumentException("object is null");
		}
		this.object = object;
		this.prettyPrinter = prettyPrinter;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public InputStream getContents() throws CoreException {
		String serialize = prettyPrinter.serialize(object);
		return new StringInputStream(serialize);
	}

	@Override
	public IPath getFullPath() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		URI uri = EcoreUtil.getURI(object);
		String fragment = uri.fragment();
		if(fragment == null || fragment.isEmpty()) {
			return root.findMember(uri.toString()).getFullPath();
		} else {
			return root.findMember(uri.toPlatformString(true)) == null ? 
					null : root.findMember(uri.toPlatformString(true)).getFullPath();
		}
	}

	@Override
	public String getName() {
		return SimpleAttributeResolver.NAME_RESOLVER.apply(object);
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}
}
