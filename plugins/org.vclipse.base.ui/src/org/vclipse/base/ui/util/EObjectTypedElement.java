package org.vclipse.base.ui.util;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.ISharedDocumentAdapter;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.SharedDocumentAdapter;
import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.internal.part.NullEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.vclipse.base.ui.BaseUiPlugin;

import com.google.common.base.Charsets;

@SuppressWarnings("rawtypes")
public class EObjectTypedElement implements ITypedElement, IEncodedStreamContentAccessor, IAdaptable  {

	private EObject object;
	private ISerializer serializer;
	private IQualifiedNameProvider nameProvider;
	
	private IEncodedStorage bufferedContents;
	private String localEncoding;
	private ISharedDocumentAdapter sharedDocumentAdapter;
	
	private final String DEFAULT_ENCODING = Charsets.UTF_8.name();
	
	public EObjectTypedElement(EObject object, ISerializer serializer) {
		this(object, serializer, null);
	}
	
	public EObjectTypedElement(EObject object, ISerializer serializer, IQualifiedNameProvider nameProvider) {
		if(object == null) {
			throw new IllegalArgumentException("object = null");
		}
		if(serializer == null) {
			throw new IllegalArgumentException("serializer = null");
		}
		
		Resource resource = object.eResource();
		localEncoding = DEFAULT_ENCODING;
		if(resource != null) {
			ResourceSet resourceset = resource.getResourceSet();
			Map<Object, Object> loadOptions = resourceset.getLoadOptions();
			localEncoding = resource instanceof XMLResource ? 
					(String)loadOptions.get(XMLResource.OPTION_ENCODING) : 
						DEFAULT_ENCODING;			
		}
		this.object = object;
		this.serializer = serializer;
		this.nameProvider = nameProvider;
	}
	
	public InputStream getContents() throws CoreException {
		if(bufferedContents == null) {
			cacheContents(EditorUtilsExtensions.getProgressMonitor());
		}
		return bufferedContents.getContents();
	}

	public void cacheContents(IProgressMonitor monitor) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		bufferedContents = new EObjectStorage(object, serializer, nameProvider, root);
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
		if(bufferedContents == null) {
			cacheContents(EditorUtilsExtensions.getProgressMonitor());
			return bufferedContents.getCharset();
		}
		return DEFAULT_ENCODING;
	}
	
	public Object getAdapter(Class adapter) {
		// IHunk
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
			EObject eobject = (EObject)element;
			Resource resource = eobject.eResource();
			if(resource == null) {
				return new URIEditorInput(EcoreUtil.getURI(eobject));
			} else {
				IFile file = ResourceUtil.getFile(resource);
				return new FileEditorInput(file);
			}
		} else {
			return new NullEditorInput();
		}	
	}

	public String getLocalEncoding() {
		return localEncoding;
	}

	@Override
	public String getName() {
		if(bufferedContents == null) {
			try {
				cacheContents(EditorUtilsExtensions.getProgressMonitor());				
			} catch(CoreException exception) {
				BaseUiPlugin.log(exception.getMessage(), exception);
			}
		}
		return bufferedContents.getName();
	}
}