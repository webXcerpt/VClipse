package org.vclipse.vcml.diff.storage;

import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.team.internal.ui.StorageTypedElement;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.vclipse.vcml.formatting.VCMLPrettyPrinter;
import org.vclipse.vcml.vcml.VCObject;

public class EObjectTypedElement extends StorageTypedElement implements IStreamContentAccessor {

	private final EObject object;
	
	private final VCMLPrettyPrinter prettyPrinter;
	
	private final IWorkspaceRoot workspaceRoot;
	
	public EObjectTypedElement(EObject object, VCMLPrettyPrinter prettyPrinter, IWorkspaceRoot workspaceRoot) {
		super(object.eResource() instanceof XMLResource ? 
				(String)object.eResource().getResourceSet().getLoadOptions().get(XMLResource.OPTION_ENCODING) : "");
		this.object = object;
		this.prettyPrinter = prettyPrinter;
		this.workspaceRoot = workspaceRoot;
	}

	@Override
	protected IStorage fetchContents(IProgressMonitor monitor) throws CoreException {
		return new EObjectStorage(object, prettyPrinter, workspaceRoot);
	}

	@Override
	protected IEditorInput getDocumentKey(Object element) {
		return element instanceof EObject ? new FileEditorInput(ResourceUtil.getFile(((EObject)element).eResource())) : null;
	}

	@Override
	public String getName() {
		if(object instanceof VCObject) {
			return ((VCObject)object).getName();
		}
		return EcoreUtil.getURI(object).toString();
	}

	@Override
	public String getType() {
		return "VCML";
	}
}
