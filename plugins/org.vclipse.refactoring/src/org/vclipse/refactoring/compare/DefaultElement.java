package org.vclipse.refactoring.compare;

import java.io.InputStream;

import org.eclipse.compare.IEncodedStreamContentAccessor;
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

public class DefaultElement implements IDiffElement, IEncodedStreamContentAccessor, IAdaptable {

	protected ISerializer serializer;
	protected IQualifiedNameProvider nameProvider;
	protected IDiffContainer parentDiffContainer;
	protected IEncodedStorage buffer;
	
	public DefaultElement() {
		
	}
	
	public DefaultElement(ISerializer serializer, IQualifiedNameProvider nameProvider) {
		if(serializer == null) {
			throw new IllegalArgumentException("Serializer is null.");
		}
		this.serializer = serializer;
		this.nameProvider = nameProvider;
	}
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getType() {
		return null;
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
