package org.vclipse.refactoring.core;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.vclipse.refactoring.ui.IUIRefactoringContext;
import org.vclipse.refactoring.ui.RefactoringUtility;
import org.vclipse.refactoring.ui.UIRefactoringContext;

public class ModelBasedChange extends CompositeChange implements IChangeCompare {

	private static final String NAME_EXTENSION_CHANGED = "changed";
	private static final String NAME_EXTENSION_CURRENT = "current";
	
	private LanguageRefactoringProcessor processor;
	private RefactoringRunner runner;
	private RefactoringUtility utility;
	
	private EObject current;
	private EObject changed;
	
	public ModelBasedChange(LanguageRefactoringProcessor processor, RefactoringRunner runner, RefactoringUtility utility) {
		super("Changes in " + processor.getContext().getSourceElement().eResource().getURI().lastSegment());
		this.processor = processor;
		this.runner = runner;
		this.utility = utility;
	}

	@Override
	public EObject getCurrent() {
		IUIRefactoringContext context = processor.getContext();
		current = utility.rootContainerCopy(context.getSourceElement(), NAME_EXTENSION_CURRENT);
		return current;
	}

	@Override
	public EObject getChanged() {
		IUIRefactoringContext context = processor.getContext();
		EObject sourceElement = context.getSourceElement();
		changed = utility.rootContainerCopy(sourceElement, NAME_EXTENSION_CHANGED);
		EObject equal = utility.getEqualTo(sourceElement, changed);
		IUIRefactoringContext contextCopy = ((UIRefactoringContext)context).copy();
		if(equal != null) {
			contextCopy.setSourceElement(equal);
		}
		runner.refactor(contextCopy);		
		return changed;
	}
}
