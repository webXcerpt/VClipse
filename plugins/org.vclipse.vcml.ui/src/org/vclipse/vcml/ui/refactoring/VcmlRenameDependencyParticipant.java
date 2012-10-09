package org.vclipse.vcml.ui.refactoring;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.vclipse.base.ui.BaseUiPlugin;
import org.vclipse.base.ui.util.VClipseResourceUtil;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class VcmlRenameDependencyParticipant extends RenameParticipant {

	// private Logger logger = Logger.getLogger(VcmlRenameDependencyParticipant.class);
	
	@Inject
	private DependencySourceUtils dependencySourceUtils;
	
	@Inject
	private VClipseResourceUtil resourceUtil;
	
	@Override
	protected boolean initialize(Object object) {
		return object instanceof IFile && 
				Sets.newHashSet(DependencySourceUtils.EXTENSION_CONSTRAINT, 
						DependencySourceUtils.EXTENSION_PRECONDITION, 
							DependencySourceUtils.EXTENSION_PROCEDURE,
								DependencySourceUtils.EXTENSION_SELECTIONCONDITION).
									contains(((IFile)object).getFileExtension());
	}

	@Override
	public String getName() {
		return VcmlRenameDependencyParticipant.class.getSimpleName();
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor progressMonitor, CheckConditionsContext context) throws OperationCanceledException {
		// TODO ? need any code here
//		ResourceChangeChecker resourceChecker = (ResourceChangeChecker)context.getChecker(ResourceChangeChecker.class);
//		if(resourceChecker != null) {
//			IResourceChangeDescriptionFactory deltaFactory = resourceChecker.getDeltaFactory();
//			IResource resource = deltaFactory.getDelta().getResource();
//			deltaFactory.delete(resource);
//		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}

	@Override
	public Change createChange(IProgressMonitor progressMonitor) throws CoreException, OperationCanceledException {
		Object[] elements = getProcessor().getElements();
		if(elements.length > 0 && elements[0] instanceof IFile) {
			IFile file = (IFile)elements[0];
			String path = file.getFullPath().toString();
			URI uri = URI.createPlatformResourceURI(path, true);
			Resource dependencyResource = new XtextResourceSet().getResource(uri, true);
			VCObject dependency = dependencySourceUtils.getDependency(dependencyResource.getURI());
			if(dependency != null) {
				dependency.eSet(VcmlPackage.eINSTANCE.getVCObject_Name(), getArguments().getNewName().replace("." + file.getFileExtension(), ""));
				try {
					dependency.eResource().save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
				} catch(IOException exception) {
					BaseUiPlugin.log(exception.getMessage(), exception);
				}				
			}
		}
		// no change is required
		// the default implementation for resource rename operation do it for us
		return super.createPreChange(progressMonitor);
	}
}
