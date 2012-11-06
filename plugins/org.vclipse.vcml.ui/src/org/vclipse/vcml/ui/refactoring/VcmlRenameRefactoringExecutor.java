package org.vclipse.vcml.ui.refactoring;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import org.eclipse.ui.IEditorPart;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.refactoring.impl.RenameElementProcessor;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.vclipse.vcml.ui.VCMLUiPlugin;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.Dependency;

import com.google.inject.Inject;

public class VcmlRenameRefactoringExecutor extends org.eclipse.xtext.ui.refactoring.ui.RenameRefactoringExecuter {

	private Logger logger = Logger.getLogger(VcmlRenameRefactoringExecutor.class);
	
	@Inject
	private DependencySourceUtils sourceUtils;
	
	@Override
	public void execute(IEditorPart editor, ProcessorBasedRefactoring refactoring) throws InterruptedException {
		RefactoringProcessor processor = refactoring.getProcessor();
		if(processor instanceof RenameElementProcessor) {
			RenameElementProcessor rep = (RenameElementProcessor)processor;
			Object[] elements = rep.getElements();
			if(elements.length > 0 && elements[0] instanceof URI) {
				if(editor instanceof XtextEditor) {
					EObject topElement = ((XtextEditor)editor).getDocument().readOnly(new IUnitOfWork<EObject, XtextResource>() {
						public EObject exec(XtextResource resource) throws Exception {
							return resource.getParseResult().getRootASTElement();
						}
					});
					URI sourceObjectUri = (URI)elements[0];
					EObject object2Rename = topElement.eResource().getResourceSet().getEObject(sourceObjectUri, true);
					if(object2Rename instanceof Dependency) {
						EObject source = sourceUtils.getSource((Dependency)object2Rename);
						if(source != null) {
							IFile file = ResourceUtil.getFile(source.eResource());
							try {
								String fileName = rep.getNewName() + "." + file.getFileExtension();
								new RenameResourceChange(file.getFullPath(), fileName).perform(new NullProgressMonitor());
							} catch (CoreException exception) {
								VCMLUiPlugin.log(exception.getMessage(), exception);
							}
						} else {
							logger.error("Rename refactoring: dependency source utility returned null for " + sourceObjectUri.toString());
						}
					} 
				}
			}
		}
		super.execute(editor, refactoring);
	}
}
