package org.vclipse.base.ui.util;

import java.io.InputStream;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.ISharedDocumentAdapter;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.SharedDocumentAdapter;
import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.ui.util.ResourceUtil;

@SuppressWarnings("rawtypes")
public class EObjectTypedElement implements IStreamContentAccessor, ITypedElement, IEncodedStreamContentAccessor, IAdaptable  {

	private EObject object;
	private ISerializer serializer;
	
	private IStorage bufferedContents;
	private final String localEncoding;
	private ISharedDocumentAdapter sharedDocumentAdapter;
	
	public EObjectTypedElement(EObject object, ISerializer serializer) {
		localEncoding = object.eResource() instanceof XMLResource ? (String)object.eResource().getResourceSet().getLoadOptions().get(XMLResource.OPTION_ENCODING) : "";
		this.object = object;
		this.serializer = serializer;
	}
	
	public InputStream getContents() throws CoreException {
		if (bufferedContents == null) {
			cacheContents(new NullProgressMonitor());
		}
		if (bufferedContents != null) {
			return bufferedContents.getContents();
		}
		return null;
	}

	public void cacheContents(IProgressMonitor monitor) throws CoreException {
		bufferedContents = fetchContents(monitor);
	}

	protected IStorage fetchContents(IProgressMonitor monitor) throws CoreException {
		return new EObjectStorage(object, serializer);
	}

	public IStorage getBufferedStorage() {
		return bufferedContents;
	}

	public Image getImage() {
		return CompareUI.getImage(getType());
	}

	public String getType() {
		String name = getName();
		if (name != null) {
			int index = name.lastIndexOf('.');
			if (index == -1)
				return ""; //$NON-NLS-1$
			if (index == (name.length() - 1))
				return ""; //$NON-NLS-1$
			return name.substring(index + 1);
		}
		return ITypedElement.FOLDER_TYPE;
	}

	public String getCharset() throws CoreException {
		if(localEncoding != null)
			return localEncoding;
		if(bufferedContents == null) {
			cacheContents(new NullProgressMonitor());
		}
		if(bufferedContents instanceof IEncodedStorage) {
			return ((IEncodedStorage)bufferedContents).getCharset();
		}
		return null;
	}
	
	public Object getAdapter(Class adapter) {
		if(adapter == ISharedDocumentAdapter.class) {
			synchronized(this) {
				if(sharedDocumentAdapter == null) {
					sharedDocumentAdapter = new SharedDocumentAdapter() {
						public IEditorInput getDocumentKey(Object element) {
							return EObjectTypedElement.this.getDocumentKey(element);
						}
						public void flushDocument(IDocumentProvider provider, IEditorInput documentKey, IDocument document, boolean overwrite) throws CoreException {
						}
					};
				}
				return sharedDocumentAdapter;
			}
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	protected IEditorInput getDocumentKey(Object element) {
		if(element instanceof EObject) {
			Resource eResource = ((EObject)element).eResource();
			IFile file = ResourceUtil.getFile(eResource);
			return element instanceof EObject ? new FileEditorInput(file) : null;			
		} else {
			return null;
		}	
	}

	public String getLocalEncoding() {
		return localEncoding;
	}

	@Override
	public String getName() {
		return "name";
	}
}