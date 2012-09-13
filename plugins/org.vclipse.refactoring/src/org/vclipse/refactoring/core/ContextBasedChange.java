package org.vclipse.refactoring.core;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.vclipse.refactoring.ui.IUIRefactoringContext;
import org.vclipse.refactoring.ui.RefactoringUtility;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ContextBasedChange extends Change implements IChangeCompare {

	@Inject
	private LanguageRefactoringProcessor processor;
	
	@Inject
	private RefactoringRunner runner;
	
	@Inject
	private RefactoringUtility utility;
	
	//@Inject
	//private Provider<ContextBasedChange> provider;
	
	@Override
	public String getName() {
		IUIRefactoringContext context = processor.getContext();
		String name = context.getLabel();
		if(name == null || name.isEmpty()) {
			name = utility.getRefactoringText(context);
		}
		return name;
	}

	@Override
	public void initializeValidationData(IProgressMonitor pm) {
		
	}

	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return RefactoringStatus.create(Status.OK_STATUS);
	}

	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		final List<EObject> results = Lists.newArrayList();
		Runnable refactoringRunnable = new Runnable() {
			@Override
			public void run() {
				final IUIRefactoringContext context = processor.getContext();
				IXtextDocument document = context.getDocument();
				List<EObject> changes = document.modify(new IUnitOfWork<List<EObject>, XtextResource>() {
					@Override
					public List<EObject> exec(XtextResource state) throws Exception {
						List<EObject> results = runner.refactor(context);
						return results;
					}
				});
				results.addAll(changes);
			}
		};
		Display.getDefault().asyncExec(refactoringRunnable);
		return null;
	}

	@Override
	public Object getModifiedElement() {
		IUIRefactoringContext context = processor.getContext();
		return context.getSourceElement();
	}
	
	public EObject getCurrent() {
		Change parent = getParent();
		if(parent instanceof ModelBasedChange) {
			EObject container = ((ModelBasedChange)parent).getCurrent();
			EObject entry = getEntry(container, processor.getContext());
			return entry == null ? container : entry;
		}
		return null;
	}
	
	public EObject getChanged() {
		Change parent = getParent();
		if(parent instanceof ModelBasedChange) {
			EObject container = ((ModelBasedChange)parent).getChanged();
			EObject entry = getEntry(container, processor.getContext());
			return entry == null ? container : entry;
		}
		return null;
	}
	
	protected EObject getEntry(EObject container, IUIRefactoringContext context) {
		IQualifiedNameProvider qualifiedNameProvider = utility.getInstance(container, IQualifiedNameProvider.class);
		EObject element = context.getSourceElement();
		QualifiedName qualifiedName = qualifiedNameProvider.getFullyQualifiedName(element);
		if(qualifiedName != null) {
			String name = qualifiedName.getLastSegment();
			List<EObject> entries = Lists.newArrayList(container.eAllContents());
			return utility.get(entries, name, element.eClass());
		}
		return null;
	}
}