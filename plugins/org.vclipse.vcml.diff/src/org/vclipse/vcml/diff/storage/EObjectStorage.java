package org.vclipse.vcml.diff.storage;

import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.util.StringInputStream;
import org.vclipse.vcml.formatting.VCMLPrettyPrinter;
import org.vclipse.vcml.vcml.VCObject;

import com.google.inject.Inject;

public class EObjectStorage implements IStorage {

	private final EObject object;
	
	private final VCMLPrettyPrinter prettyPrinter;
	
	private final IWorkspaceRoot workspaceRoot;
	
	@Inject
	public EObjectStorage(EObject object, VCMLPrettyPrinter prettyPrinter, IWorkspaceRoot workspaceRoot) {
		if(object == null) {
			throw new IllegalArgumentException("object is null");
		}
		this.object = object;
		this.prettyPrinter = prettyPrinter;
		this.workspaceRoot = workspaceRoot;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public InputStream getContents() throws CoreException {
		return new StringInputStream(prettyPrinter.prettyPrint(object));
	}

	@Override
	public IPath getFullPath() {
		URI uri = EcoreUtil.getURI(object);
		String fragment = uri.fragment();
		if(fragment == null || fragment.isEmpty()) {
			return workspaceRoot.findMember(uri.toString()).getFullPath();
		} else {
			return workspaceRoot.findMember(uri.toPlatformString(true)) == null ? null : workspaceRoot.findMember(uri.toPlatformString(true)).getFullPath();
		}
	}

	@Override
	public String getName() {
		if(object instanceof VCObject) {
			return ((VCObject)object).getName();
		}
		return null;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}
}
