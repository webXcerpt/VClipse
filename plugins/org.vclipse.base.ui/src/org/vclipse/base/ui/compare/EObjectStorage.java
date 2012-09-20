package org.vclipse.base.ui.compare;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.util.SimpleAttributeResolver;
import org.eclipse.xtext.util.StringInputStream;
import org.vclipse.base.ui.BaseUiPlugin;

import com.google.common.base.Charsets;

public class EObjectStorage implements IEncodedStorage {

	private final EObject object;
	private final ISerializer serializer;
	private final IQualifiedNameProvider nameProvider;
	private final IWorkspaceRoot root;
	
	public EObjectStorage(EObject object, ISerializer serializer) {
		this(object, serializer, null, null);
	}
	
	public EObjectStorage(EObject object, ISerializer serializer, IQualifiedNameProvider qualifiedNameProvider, IWorkspaceRoot root) {
		// initialize from resources plug-in
		this.root = root == null ? ResourcesPlugin.getWorkspace().getRoot() : root;
		this.object = object;
		this.serializer = serializer;
		this.nameProvider = qualifiedNameProvider;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public InputStream getContents() throws CoreException {
		String serialized = serializer.serialize(object);
		try {
			return new StringInputStream(serialized, getCharset());			
		} catch(UnsupportedEncodingException exception) {
			BaseUiPlugin.log(exception.getMessage(), exception);
			return new StringInputStream(serialized);
		}
	}

	@Override
	public IPath getFullPath() {
		URI uri = EcoreUtil.getURI(object);
		String fragment = uri.fragment();
		if(fragment == null || fragment.isEmpty()) {
			return root.findMember(uri.toString()).getFullPath();
		} else {
			IResource member = root.findMember(uri.toPlatformString(true));
			return member == null ? null : member.getFullPath();
		}
	}

	@Override
	public String getName() {
		if(nameProvider != null) {
			QualifiedName qualifiedName = 
					nameProvider.getFullyQualifiedName(object);
			if(qualifiedName != null) {
				return qualifiedName.getLastSegment();
			}
		} 
		String name = SimpleAttributeResolver.NAME_RESOLVER.apply(object);
		return name == null ? object.eClass().getName() : name;
	}

	@Override
	public boolean isReadOnly() {
		// there are currently no use 
		// cases for writing available => true 
		return true;
	}

	@Override
	public String getCharset() throws CoreException {
		return Charsets.UTF_8.name();
	}
}
