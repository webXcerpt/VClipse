package org.vclipse.base.ui.compare;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.ISharedDocumentAdapter;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.SharedDocumentAdapter;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
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
import org.vclipse.base.ui.util.EditorUtilsExtensions;

import com.google.common.base.Charsets;

@SuppressWarnings("rawtypes")
public class EObjectTypedElement implements IDiffElement, IEncodedStreamContentAccessor, IAdaptable  {

	private EObject object;
	private ISerializer serializer;
	private IQualifiedNameProvider nameProvider;
	
	private IEncodedStorage bufferedContents;
	private ISharedDocumentAdapter sharedDocumentAdapter;
	private String localEncoding;
	
	private IDiffContainer parentContainer;
	private int changeKind;
	
	private final String DEFAULT_ENCODING = Charsets.UTF_8.name();
	
	public static EObjectTypedElement getEmpty() {
		return new EObjectTypedElement();
	}
	
	private EObjectTypedElement() {

	}
	
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
		if(object != null && serializer != null) {
			bufferedContents = new EObjectStorage(object, serializer, nameProvider, root);			
		} else {
			bufferedContents = new IEncodedStorage() {
				@Override
				public InputStream getContents() throws CoreException {
					return null;
				}
				@Override
				public IPath getFullPath() {
					return null;
				}
				@Override
				public String getName() {
					return "no name";
				}
				@Override
				public boolean isReadOnly() {
					return false;
				}
				@Override
				public Object getAdapter(Class adapter) {
					return null;
				}
				@Override
				public String getCharset() throws CoreException {
					return null;
				}
			};
		}
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

	protected EObject getEObject() {
		return object;
	}
	
	protected IEditorInput getDocumentKey(Object element) {
		if(element instanceof EObjectTypedElement) {
			EObjectTypedElement typedElement = (EObjectTypedElement)element;
			EObject eobject = typedElement.getEObject();
			if(eobject != null) {
				Resource resource = eobject.eResource();
				if(resource != null) {
					IFile file = ResourceUtil.getFile(resource);
					return new FileEditorInput(file);					
				}
			}
		}
		return new NullEditorInput();
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
		String name = bufferedContents.getName();
		return name == null ? "no name" : name;
	}

	@Override
	public IDiffContainer getParent() {
		return parentContainer;
	}

	@Override
	public void setParent(IDiffContainer parent) {
		this.parentContainer = parent;
	}
	
	public void setKind(int kind) {
		this.changeKind = kind;
	}
	
	@Override
	public int getKind() {
		return changeKind;
	}
}