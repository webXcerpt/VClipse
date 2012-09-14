package org.vclipse.refactoring.core;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.vclipse.refactoring.ui.IUIRefactoringContext;
import org.vclipse.refactoring.ui.RefactoringUtility;
import org.vclipse.refactoring.ui.UIRefactoringContext;

import com.google.common.collect.Lists;

public class ModelBasedChange extends CompositeChange implements IChangeCompare {

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
		EObject element = context.getSourceElement();
		current = EcoreUtil.getRootContainer(element);
		return current;
	}

	@Override
	public EObject getChanged() {
		IUIRefactoringContext context = processor.getContext();
		EObject sourceElement = context.getSourceElement();
		changed = utility.rootContainerCopy(sourceElement);
		IQualifiedNameProvider nameProvider = utility.getInstance(sourceElement, IQualifiedNameProvider.class);
		QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(sourceElement);
		if(qualifiedName != null) {
			String name = qualifiedName.getLastSegment();
			EObject toChange = utility.getEntry(Lists.newArrayList(changed.eAllContents()), name, sourceElement.eClass());
			IUIRefactoringContext contextCopy = ((UIRefactoringContext)context).copy();
			if(toChange != null) {
				contextCopy.setSourceElement(toChange);
			}
			runner.refactor(contextCopy);
		}	
		return changed;
	}
}
